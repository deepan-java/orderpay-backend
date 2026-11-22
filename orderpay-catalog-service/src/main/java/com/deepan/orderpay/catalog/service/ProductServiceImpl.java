package com.deepan.orderpay.catalog.service;

import com.deepan.orderpay.catalog.aop.Audit;
import com.deepan.orderpay.catalog.dto.*;
import com.deepan.orderpay.catalog.entity.Category;
import com.deepan.orderpay.catalog.entity.Product;
import com.deepan.orderpay.catalog.exception.InsufficientStockException;
import com.deepan.orderpay.catalog.exception.InvalidRequestException;
import com.deepan.orderpay.catalog.exception.ResourceNotFoundException;
import com.deepan.orderpay.catalog.mapper.ProductMapper;
import com.deepan.orderpay.catalog.repository.CategoryRepository;
import com.deepan.orderpay.catalog.repository.ProductRepository;
import com.deepan.orderpay.catalog.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Audit(action = "product.create")
    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = findCategoryOrThrow(request.categoryId());
        Product saved = productRepository.save(productMapper.toEntity(request, category));
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    private static final List<String> SORTABLE_FIELDS = List.of("id", "name", "price", "stockQuantity");

    @Override
    public PageResponse<ProductResponse> search(
            ProductSearchCriteria criteria,
            Pageable pageable) {

        validateCriteria(criteria);
        validateSort(pageable);

        Pageable stable = withStableOrder(pageable);

        Page<Product> page =
                productRepository.findAll(
                        ProductSpecifications.from(criteria),
                        stable
                );

        return PageResponse.from(
                page.map(productMapper::toResponse)
        );
    }

    private void validateSort(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new InvalidRequestException(
                        "Cannot sort by '" + order.getProperty() + "'. Allowed fields: " + SORTABLE_FIELDS);
            }
        });
    }

    private Pageable withStableOrder(Pageable pageable) {
        if (pageable.getSort().getOrderFor("id") != null) {
            return pageable;
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(Sort.by("id")));
    }

    private void validateCriteria(ProductSearchCriteria c) {

        if (c.minPrice() != null && c.minPrice().signum() < 0) {
            throw new InvalidRequestException(
                    "minPrice cannot be negative");
        }

        if (c.maxPrice() != null && c.maxPrice().signum() < 0) {
            throw new InvalidRequestException(
                    "maxPrice cannot be negative");
        }

        if (c.minPrice() != null
                && c.maxPrice() != null
                && c.minPrice().compareTo(c.maxPrice()) > 0) {

            throw new InvalidRequestException(
                    "minPrice cannot be greater than maxPrice");
        }
    }

    @Audit(action = "product.update")
    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        Category category = findCategoryOrThrow(request.categoryId());
        productMapper.updateEntity(product, request, category);
        return productMapper.toResponse(product);   // no save() needed: dirty checking
    }

    @Audit(action = "product.delete")
    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.delete(findProductOrThrow(id));
    }

    @Audit(action = "product.patch")
    @Override
    @Transactional
    public ProductResponse patch(Long id, ProductPatchRequest request) {
        Product product = findProductOrThrow(id);
        boolean changed = false;

        if (request.name() != null) {
            product.setName(request.name().trim());
            changed = true;
        }
        if (request.description() != null) {
            product.setDescription(request.description());
            changed = true;
        }
        if (request.price() != null) {
            product.setPrice(request.price());
            changed = true;
        }
        if (request.stockQuantity() != null) {
            product.setStockQuantity(request.stockQuantity());
            changed = true;
        }
        if (request.categoryId() != null) {
            product.setCategory(findCategoryOrThrow(request.categoryId()));
            changed = true;
        }
        if (!changed) {
            throw new InvalidRequestException("At least one field must be provided");
        }
        return productMapper.toResponse(product);   // dirty checking writes the UPDATE
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Override
    public PageResponse<ProductResponse> searchByCategory(Long categoryId, Pageable pageable) {
        findCategoryOrThrow(categoryId);                      // 404 if the category doesn't exist
        return search(new ProductSearchCriteria(null, categoryId, null, null), pageable);
    }

    @Override
    public List<CheapestProductResponse> cheapestPerCategory() {
        return productRepository.findCheapestPerCategory()
                .stream()
                .map(r -> new CheapestProductResponse(
                        r.getCategoryId(),
                        r.getCategoryName(),
                        r.getProductId(),
                        r.getProductName(),
                        r.getPrice()
                ))
                .toList();
    }
    @Override
    public PageResponse<ProductResponse> lowStock(
            int threshold, Pageable pageable) {

        if (threshold < 0) {
            throw new InvalidRequestException(
                    "threshold cannot be negative");
        }

        // The query defines its own order.
        Pageable paging = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return PageResponse.from(
                productRepository.findLowStock(threshold, paging)
                        .map(productMapper::toResponse)
        );
    }

    @Audit(action = "product.adjust-prices")
    @Override
    @Transactional
    public PriceAdjustmentResult adjustPrices(
            Long categoryId, BigDecimal percent) {

        findCategoryOrThrow(categoryId);

        BigDecimal factor = BigDecimal.ONE.add(
                percent.divide(BigDecimal.valueOf(100))
        );

        int updated = productRepository.adjustPriceByCategory(
                categoryId,
                factor,
                Instant.now());

        return new PriceAdjustmentResult(categoryId, updated);
    }

    @Override
    public List<CategoryPriceStats> priceStats() {
        return productRepository.priceStatsByCategory();
    }

    @Audit(action = "product.reserve-stock")
    @Override
    @Transactional
    public StockReservationResponse reserveStock(Long id, int quantity) {
        Product product = findProductOrThrow(id);
        product.reserve(quantity);           // dirty checking writes the UPDATE at commit
        return new StockReservationResponse(product.getId(), quantity, product.getStockQuantity());
    }

    @Override
    @Transactional
    public StockReservationResponse reserveStockPessimistic(
            Long id, int quantity) {

        Product product = productRepository.findByIdForUpdate(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product", id)
                );
        product.reserve(quantity);
        return new StockReservationResponse(
                product.getId(),
                quantity,
                product.getStockQuantity()
        );
    }
    @Override
    @Transactional
    public StockReservationResponse reserveStockAtomic(
            Long id, int quantity) {

        if (quantity <= 0) {
            throw new InvalidRequestException(
                    "quantity must be positive"
            );
        }

        int updated = productRepository.decrementStockIfAvailable(
                id,
                quantity,
                Instant.now()
        );

        if (updated == 1) {
            int remainingStock = productRepository.findStockById(id);

            return new StockReservationResponse(
                    Math.toIntExact(id),
                    quantity,
                    remainingStock
            );
        }

        // No rows updated: check whether the product exists.
        Product product = findProductOrThrow(id);

        // Product exists, but its stock was insufficient.
        throw new InsufficientStockException(
                id,
                quantity,
                product.getStockQuantity()
        );
    }
}