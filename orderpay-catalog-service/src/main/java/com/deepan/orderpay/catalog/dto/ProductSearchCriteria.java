package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public record ProductSearchCriteria(
        String name,
        Long categoryId ,
        BigDecimal minPrice,
        BigDecimal maxPrice ){}
