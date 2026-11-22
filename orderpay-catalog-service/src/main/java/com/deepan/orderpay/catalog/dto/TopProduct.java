package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public record TopProduct(Long categoryId, String categoryName, Long productId,
                         String productName, BigDecimal price, int rank) {}
