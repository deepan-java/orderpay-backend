package com.deepan.orderpay.catalog.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PriceAdjustmentRequest(
        @NotNull(message = "Percent is required")
        @DecimalMin(value = "-90.0",
                message = "Percent must be at least -90")
        @DecimalMax(value = "100.0",
                message = "Percent must be at most 100")
        BigDecimal percent
) {}