package com.deepan.orderpay.auth.controller;

import com.deepan.orderpay.auth.dto.*;
import com.deepan.orderpay.auth.security.AuthenticatedUser;
import com.deepan.orderpay.auth.service.AccountService;
import com.deepan.orderpay.auth.service.LoginService;
import com.deepan.orderpay.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final LoginService loginService;
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(
            Authentication authentication) {

        return ResponseEntity.ok(
                new CurrentUserResponse(
                        authentication.getName(),
                        authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )
        );
    }
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(loginService.login(request));
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshRequest request
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(loginService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshRequest request
    ) {
        loginService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal AuthenticatedUser principal) {
        accountService.logoutAll(principal.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AuthenticatedUser principal,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(principal.userId(), request);
        return ResponseEntity.noContent().build();
    }

}
