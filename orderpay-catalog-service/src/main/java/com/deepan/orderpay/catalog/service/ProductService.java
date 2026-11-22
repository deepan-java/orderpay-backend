package com.deepan.orderpay.catalog.service;

import com.deepan.orderpay.catalog.dto.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse getById(Long id);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    PageResponse<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable);
    ProductResponse patch(Long id, ProductPatchRequest request);
    PageResponse<ProductResponse> searchByCategory(Long categoryId, Pageable pageable);
    List<CheapestProductResponse> cheapestPerCategory();
    PageResponse<ProductResponse> lowStock(
            int threshold, Pageable pageable);
    PriceAdjustmentResult adjustPrices(
            Long categoryId, BigDecimal percent);
    List<CategoryPriceStats> priceStats();
    StockReservationResponse reserveStock(Long id, int quantity);
    StockReservationResponse reserveStockPessimistic(
            Long id, int quantity);
    StockReservationResponse reserveStockAtomic(
            Long id, int quantity);
}