package com.deepan.orderpay.catalog.dto;

public record StockReservationResponse(
        int  productId,
        int reserved,
        int remainingStock
) {
}
