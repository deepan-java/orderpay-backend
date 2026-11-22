package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public record CategoryStockValue(Long categoryId, String categoryName,
                                 long productCount, long totalUnits, BigDecimal stockValue) {}

