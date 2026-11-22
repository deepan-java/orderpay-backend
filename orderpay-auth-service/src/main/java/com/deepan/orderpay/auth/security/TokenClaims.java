package com.deepan.orderpay.auth.security;

import java.time.Instant;
import java.util.Set;

public record TokenClaims(
        Long userId,
        String email,
        Set<String> roles,
        String tokenId,
        Instant issuedAt,
        Instant expiresAt
) {}
