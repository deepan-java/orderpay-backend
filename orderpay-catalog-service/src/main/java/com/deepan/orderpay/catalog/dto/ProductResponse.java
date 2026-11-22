package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id, String name, String description, BigDecimal price,
        int stockQuantity, Long categoryId, String categoryName) {}