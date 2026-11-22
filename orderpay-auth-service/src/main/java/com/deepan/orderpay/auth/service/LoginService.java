
package com.deepan.orderpay.auth.service;

import com.deepan.orderpay.auth.dto.LoginRequest;
import com.deepan.orderpay.auth.dto.TokenResponse;
import com.deepan.orderpay.auth.entity.User;
import com.deepan.orderpay.auth.exception.InvalidCredentialsException;
import com.deepan.orderpay.auth.repository.UserRepository;
import com.deepan.orderpay.auth.security.IssuedToken;

import com.deepan.orderpay.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken
                            .unauthenticated(email, request.password())
            );

        } catch (AuthenticationException e) {

            log.warn(
                    "Failed login attempt: {}",
                    e.getClass().getSimpleName()
            );

            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        return tokensFor(
                user,
                refreshTokenService.startFamily(user)
        );
    }

    public TokenResponse refresh(String refreshToken) {

        RefreshTokenService.Rotation rotation =
                refreshTokenService.rotate(refreshToken);

        return tokensFor(
                rotation.user(),
                rotation.refreshToken()
        );
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private TokenResponse tokensFor(
            User user,
            String refreshToken
    ) {

        IssuedToken access = jwtService.issueAccessToken(user);

        return new TokenResponse(
                access.value(),
                refreshToken,
                "Bearer",
                access.expiresInSeconds()
        );
    }
}
