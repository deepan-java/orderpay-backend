package com.deepan.orderpay.catalog.mapper;

import com.deepan.orderpay.catalog.dto.ProductRequest;
import com.deepan.orderpay.catalog.dto.ProductResponse;
import com.deepan.orderpay.catalog.entity.Category;
import com.deepan.orderpay.catalog.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request,Category category) {
        return new Product(request.name(), request.description(), request.price(),
                request.stockQuantity(), category);
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse((long) product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getStockQuantity(),
                product.getCategory().getId(), product.getCategory().getName());
    }

    public void updateEntity(Product product, ProductRequest request, Category category) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
    }
}