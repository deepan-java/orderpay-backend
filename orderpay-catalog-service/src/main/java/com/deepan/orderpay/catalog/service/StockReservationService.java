package com.deepan.orderpay.catalog.service;

import com.deepan.orderpay.catalog.dto.StockReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockReservationService {

    static final int MAX_ATTEMPTS = 3;

    private final ProductService productService;

    public StockReservationResponse reserve(
            Long productId, int quantity) {

        for (int attempt = 1; ; attempt++) {
            try {
                return productService.reserveStock(
                        productId, quantity
                );
            } catch (OptimisticLockingFailureException ex) {
                if (attempt >= MAX_ATTEMPTS) {
                    throw ex;
                }

                pause(attempt);
            }
        }
    }

    private void pause(int attempt) {
        try {
            Thread.sleep(
                    ThreadLocalRandom.current()
                            .nextLong(5, 20L * attempt + 5)
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while retrying", ex
            );
        }
    }
}
