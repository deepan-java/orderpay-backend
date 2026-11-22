package com.deepan.orderpay.catalog.controller;

import com.deepan.orderpay.catalog.dto.CategoryStockValue;
import com.deepan.orderpay.catalog.dto.InventorySummary;
import com.deepan.orderpay.catalog.dto.TopProduct;
import com.deepan.orderpay.catalog.service.ProductReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ProductReportService reportService;

    @GetMapping("/inventory-summary")
    public ResponseEntity<InventorySummary> inventorySummary() {
        return ResponseEntity.ok(
                reportService.inventorySummary()
        );
    }
    @GetMapping("/stock-value-by-category")
    public List<CategoryStockValue> getStockValueByCategory() {
        return reportService.getStockValueByCategory();
    }

    @GetMapping("/top-products")
    public List<TopProduct> getTopProducts(
            @RequestParam(defaultValue = "3") int n) {
        return reportService.getTopProducts(n);
    }
}