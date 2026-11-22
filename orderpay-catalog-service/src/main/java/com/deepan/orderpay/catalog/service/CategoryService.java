package com.deepan.orderpay.catalog.service;

import com.deepan.orderpay.catalog.dto.CategoryProductCount;
import com.deepan.orderpay.catalog.dto.CategoryRequest;
import com.deepan.orderpay.catalog.dto.CategoryResponse;

import java.util.List;

public interface  CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse getById(Long id);
    List<CategoryResponse> getAll();
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
    List<CategoryProductCount> productCounts();
}
