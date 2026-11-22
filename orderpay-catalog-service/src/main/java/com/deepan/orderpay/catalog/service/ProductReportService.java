package com.deepan.orderpay.catalog.service;

import com.deepan.orderpay.catalog.dto.CategoryStockValue;
import com.deepan.orderpay.catalog.dto.InventorySummary;
import com.deepan.orderpay.catalog.dto.TopProduct;
import com.deepan.orderpay.catalog.repository.ProductReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReportService {

    private final ProductReportRepository reportRepository;

    public InventorySummary inventorySummary() {
        return reportRepository.inventorySummary();
    }

    public List<CategoryStockValue> getStockValueByCategory() {
        return reportRepository.stockValueByCategory();
    }
    public List<TopProduct> getTopProducts(int n) {
        if (n < 1 || n > 10) {
            throw new IllegalArgumentException(
                    "n must be between 1 and 10"
            );
        }

        return reportRepository.topProductsPerCategory(n);
    }
}
