package com.deepan.orderpay.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100 ,message = "Category name must be atmost 11 characters")
        String name,
        @Size(max = 500 ,message = "Description must be atmost 100 characters")
        String description) {}
