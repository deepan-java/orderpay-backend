package com.deepan.orderpay.auth.config;

import com.deepan.orderpay.auth.security.JwtProperties;
import com.deepan.orderpay.auth.security.RefreshTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        RefreshTokenProperties.class
})
public class JwtConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
