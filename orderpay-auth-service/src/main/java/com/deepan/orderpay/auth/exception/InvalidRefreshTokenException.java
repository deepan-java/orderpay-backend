package com.deepan.orderpay.auth.exception;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super("INVALID_REFRESH_TOKEN", "Invalid or expired refresh token");
    }
}
