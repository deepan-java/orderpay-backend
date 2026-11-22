
package com.deepan.orderpay.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    public static final String INVALID_TOKEN_ATTRIBUTE =
            "orderpay.invalidToken";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        if (Boolean.TRUE.equals(
                request.getAttribute(INVALID_TOKEN_ATTRIBUTE))) {

            response.setHeader(
                    HttpHeaders.WWW_AUTHENTICATE,
                    "Bearer error=\"invalid_token\""
            );

            SecurityErrorWriter.write(
                    request,
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "INVALID_TOKEN",
                    "The access token is invalid or has expired"
            );

        } else {

            response.setHeader(
                    HttpHeaders.WWW_AUTHENTICATE,
                    "Bearer"
            );

            SecurityErrorWriter.write(
                    request,
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "UNAUTHENTICATED",
                    "Authentication is required to access this resource"
            );
        }
    }
}
