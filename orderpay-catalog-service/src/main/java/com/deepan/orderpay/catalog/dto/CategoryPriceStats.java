package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public record CategoryPriceStats(
        Long categoryId,
        String categoryName,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal avgPrice,
        Long productCount
) {}