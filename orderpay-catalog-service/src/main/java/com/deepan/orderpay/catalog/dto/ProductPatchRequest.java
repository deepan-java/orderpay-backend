package com.deepan.orderpay.catalog.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductPatchRequest(

        @Pattern(
                regexp = ".*\\S.*",
                message = "Product name must not be blank"
        )
        @Size(max = 150, message = "Product name must be at most 150 characters")
        String name,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        @Positive(message = "Price must be greater than zero")
        @Digits(
                integer = 10,
                fraction = 2,
                message = "Price allows up to 10 digits and 2 decimals"
        )
        BigDecimal price,

        @PositiveOrZero(message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        Long categoryId

) {}