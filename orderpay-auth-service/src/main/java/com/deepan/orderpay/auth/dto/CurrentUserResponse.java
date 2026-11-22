package com.deepan.orderpay.auth.dto;

import java.util.List;

public record CurrentUserResponse(
        String email,
        List<String> authorities
) {}