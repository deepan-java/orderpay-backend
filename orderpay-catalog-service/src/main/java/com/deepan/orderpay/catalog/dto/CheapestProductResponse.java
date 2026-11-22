package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public record CheapestProductResponse(
        Long categoryId,
        String categoryName,
        Long productId,
        String productName,
        BigDecimal price
) {}