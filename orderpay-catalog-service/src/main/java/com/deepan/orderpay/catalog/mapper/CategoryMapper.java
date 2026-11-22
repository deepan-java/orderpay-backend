package com.deepan.orderpay.catalog.mapper;

import com.deepan.orderpay.catalog.dto.CategoryRequest;
import com.deepan.orderpay.catalog.dto.CategoryResponse;
import com.deepan.orderpay.catalog.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return new Category(request.name(), request.description());
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    public void updateEntity(Category category, CategoryRequest request) {
        category.setName(request.name());
        category.setDescription(request.description());
    }
}