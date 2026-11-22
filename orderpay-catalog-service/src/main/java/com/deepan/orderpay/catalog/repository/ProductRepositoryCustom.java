package com.deepan.orderpay.catalog.repository;

import com.deepan.orderpay.catalog.dto.CategoryPriceStats;

import java.util.List;

public interface ProductRepositoryCustom {
    List<CategoryPriceStats> priceStatsByCategory();
}
