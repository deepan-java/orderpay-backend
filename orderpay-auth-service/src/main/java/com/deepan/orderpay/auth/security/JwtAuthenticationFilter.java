package com.deepan.orderpay.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String token = extractBearerToken(request);

        if (token != null) {
            try {
                TokenClaims claims = jwtService.parse(token);

                SecurityContextHolder.setContext(
                        contextFor(claims, request)
                );

            } catch (InvalidTokenException e) {
                request.setAttribute(
                        ApiAuthenticationEntryPoint.INVALID_TOKEN_ATTRIBUTE,
                        Boolean.TRUE
                );

                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null
                || header.length() <= BEARER_PREFIX.length()
                || !header.regionMatches(
                true, 0,
                BEARER_PREFIX, 0,
                BEARER_PREFIX.length()
        )) {
            return null;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();

        return token.isEmpty() ? null : token;
    }

    private SecurityContext contextFor(
            TokenClaims claims,
            HttpServletRequest request
    ) {

        AuthenticatedUser principal = new AuthenticatedUser(
                claims.userId(),
                claims.email(),
                claims.roles()
        );

        List<SimpleGrantedAuthority> authorities =
                claims.roles().stream()
                        .sorted()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        principal,
                        null,
                        authorities
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        return context;
    }
}