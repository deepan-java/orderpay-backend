package com.deepan.orderpay.catalog.dto;

import java.math.BigDecimal;

public interface CheapestProductRow {
    Long getCategoryId();
    String getCategoryName();
    Long getProductId();
    String getProductName();
    BigDecimal getPrice();
}
