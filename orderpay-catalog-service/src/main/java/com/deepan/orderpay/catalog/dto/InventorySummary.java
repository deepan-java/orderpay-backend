package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public record InventorySummary(
        long productCount,
        long totalUnits,
        BigDecimal totalValue,
        BigDecimal averagePrice
) {}
