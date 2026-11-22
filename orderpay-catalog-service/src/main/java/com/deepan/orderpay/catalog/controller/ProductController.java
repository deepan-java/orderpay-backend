package com.deepan.orderpay.catalog.controller;

import com.deepan.orderpay.catalog.dto.*;
import com.deepan.orderpay.catalog.exception.ApiError;
import com.deepan.orderpay.catalog.service.ProductService;
import com.deepan.orderpay.catalog.service.StockReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog operations")   // on the class
public class ProductController {

    private final ProductService productService;
    private final StockReservationService stockReservationService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);       // 201 + Location header
    }

    @Operation(summary = "Get a product by id")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));        // 200
    }

    @Operation(
            summary = "Search products with filters, paging and sorting"
    )
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        var criteria = new ProductSearchCriteria(name, categoryId, minPrice, maxPrice);
        return ResponseEntity.ok(productService.search(criteria, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request)); // 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();                   // 204
    }

    @Operation(summary = "Partially update a product (only the fields sent)")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> patch(@PathVariable Long id,
                                                 @Valid @RequestBody ProductPatchRequest request) {
        return ResponseEntity.ok(productService.patch(id, request));
    }

    @GetMapping("/reports/cheapest-per-category")
    public ResponseEntity<List<CheapestProductResponse>>
    cheapestPerCategory() {
        return ResponseEntity.ok(productService.cheapestPerCategory());
    }
    @GetMapping("/reports/low-stock")
    public ResponseEntity<PageResponse<ProductResponse>> lowStock(
            @RequestParam(defaultValue = "5") int threshold,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                productService.lowStock(threshold, pageable));
    }


    @GetMapping("/reports/price-stats")
    public ResponseEntity<List<CategoryPriceStats>> priceStats() {
        return ResponseEntity.ok(
                productService.priceStats());
    }

    @PostMapping("/{id}/stock-reservations")
    public ResponseEntity<StockReservationResponse> reserveStock(
            @PathVariable Long id,
            @Valid @RequestBody StockReservationRequest request) {

        return ResponseEntity.ok(
                stockReservationService.reserve(
                        id, request.quantity()
                )
        );
    }

}