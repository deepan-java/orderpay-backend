package com.deepan.orderpay.auth.exception;

public abstract class BusinessException extends RuntimeException {

    private final String errorCode;

    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static class InvalidCredentialsException extends BusinessException {

        public InvalidCredentialsException() {
            super("INVALID_CREDENTIALS", "Invalid email or password");
        }
    }
}