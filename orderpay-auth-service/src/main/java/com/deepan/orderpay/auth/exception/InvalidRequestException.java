package com.deepan.orderpay.auth.exception;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException(String message) {
        super("INVALID_REQUEST", message);
    }
}
