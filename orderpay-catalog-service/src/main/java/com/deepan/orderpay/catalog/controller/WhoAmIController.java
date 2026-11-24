package com.deepan.orderpay.catalog.controller;

import com.deepan.orderpay.catalog.dto.CurrentCaller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class WhoAmIController {

    @GetMapping("/me")
    public ResponseEntity<CurrentCaller> me(@AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return ResponseEntity.ok(new CurrentCaller(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList()));
    }
}
