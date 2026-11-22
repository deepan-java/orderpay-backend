package com.deepan.orderpay.catalog.dto;

public record CategoryProductCount(
        Long categoryId,
        String categoryName,
        Long productCount
) {}
