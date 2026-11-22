package com.deepan.orderpay.auth.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.security.refresh-token")
public record RefreshTokenProperties(@NotNull Duration ttl) {
}