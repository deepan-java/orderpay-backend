package com.deepan.orderpay.auth.exception;

public class InvalidPasswordChangeException extends BusinessException {
    public InvalidPasswordChangeException(String message) {
        super("INVALID_PASSWORD_CHANGE", message);
    }
}
