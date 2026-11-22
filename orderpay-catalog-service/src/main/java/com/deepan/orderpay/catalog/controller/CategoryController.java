package com.deepan.orderpay.catalog.controller;

import com.deepan.orderpay.catalog.dto.*;
import com.deepan.orderpay.catalog.service.CategoryService;
import com.deepan.orderpay.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));   // 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();                  // 204
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<PageResponse<ProductResponse>> products(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.searchByCategory(id, pageable));
    }

    @GetMapping("/product-counts")
    public ResponseEntity<List<CategoryProductCount>> productCounts() {
        return ResponseEntity.ok(categoryService.productCounts());
    }

    @PostMapping("/{id}/price-adjustments")
    public ResponseEntity<PriceAdjustmentResult> adjustPrices(
            @PathVariable Long id,
            @Valid @RequestBody PriceAdjustmentRequest request) {

        return ResponseEntity.ok(
                productService.adjustPrices(id, request.percent()));
    }
}
