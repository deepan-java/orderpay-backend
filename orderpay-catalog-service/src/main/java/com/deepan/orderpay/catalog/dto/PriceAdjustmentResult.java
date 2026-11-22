package com.deepan.orderpay.catalog.dto;

public record PriceAdjustmentResult(
        Long categoryId,
        int updatedProducts
) {}