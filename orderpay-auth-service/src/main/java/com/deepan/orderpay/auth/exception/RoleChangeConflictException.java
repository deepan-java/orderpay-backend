package com.deepan.orderpay.auth.exception;

public class RoleChangeConflictException extends BusinessException {
    public RoleChangeConflictException(String message) {
        super("ROLE_CHANGE_CONFLICT", message);
    }
}
