package com.deepan.orderpay.auth.security;

import java.security.Principal;
import java.util.Set;

public record AuthenticatedUser(
        Long userId,
        String email,
        Set<String> roles
) implements Principal {

    @Override
    public String getName() {
        return email;
    }
}