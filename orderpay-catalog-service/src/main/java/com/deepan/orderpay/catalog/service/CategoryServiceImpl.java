    package com.deepan.orderpay.catalog.service;
    import com.deepan.orderpay.catalog.aop.Audit;
    import com.deepan.orderpay.catalog.dto.*;
    import com.deepan.orderpay.catalog.entity.*;
    import com.deepan.orderpay.catalog.exception.DuplicateResourceException;
    import com.deepan.orderpay.catalog.exception.ResourceInUseException;
    import com.deepan.orderpay.catalog.exception.ResourceNotFoundException;
    import com.deepan.orderpay.catalog.mapper.*;
    import com.deepan.orderpay.catalog.repository.*;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import java.util.List;

    @Service
    @RequiredArgsConstructor
    @Transactional(readOnly = true)
    public class CategoryServiceImpl implements CategoryService {

        private final CategoryRepository categoryRepository;
        private final CategoryMapper categoryMapper;
        private final ProductRepository productRepository;

        @Audit(action = "category.create")
        @Override
        @Transactional
        public CategoryResponse create(CategoryRequest request) {
            if (categoryRepository.existsByNameIgnoreCase(request.name())) {
                throw new DuplicateResourceException("Category", "name", request.name());
            }
            Category saved = categoryRepository.save(categoryMapper.toEntity(request));
            return categoryMapper.toResponse(saved);
        }

        @Override
        public CategoryResponse getById(Long id) {
            return categoryMapper.toResponse(findOrThrow(id));
        }

        @Override
        public List<CategoryResponse> getAll() {
            return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
        }

        @Audit(action = "category.update")
        @Override
        @Transactional
        public CategoryResponse update(Long id, CategoryRequest request) {
            Category category = findOrThrow(id);
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
                throw new DuplicateResourceException("Category", "name", request.name());
            }
            categoryMapper.updateEntity(category, request);
            return categoryMapper.toResponse(category);
        }

        @Audit(action = "category.delete")
        @Override
        @Transactional
        public void delete(Long id) {
            Category category = findOrThrow(id);
            if (productRepository.existsByCategoryId(id)) {
                throw new ResourceInUseException("Category", id, "products");
            }
            categoryRepository.delete(category);
        }

        private Category findOrThrow(Long id) {
            return categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        }

        @Override
        public List<CategoryProductCount> productCounts() {
            return categoryRepository.findProductCounts();
        }
    }