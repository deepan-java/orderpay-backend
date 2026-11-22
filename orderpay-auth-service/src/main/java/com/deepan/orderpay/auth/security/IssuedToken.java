package com.deepan.orderpay.auth.security;

public record IssuedToken(String value, long expiresInSeconds) {

    @Override
    public String toString() {
        return "IssuedToken[value=***, expiresInSeconds="
                + expiresInSeconds + "]";
    }
}
