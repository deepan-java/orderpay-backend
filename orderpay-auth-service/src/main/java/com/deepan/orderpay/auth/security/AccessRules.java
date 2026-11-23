package com.deepan.orderpay.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class AccessRules {

    public boolean isSelfOrAdmin(Authentication authentication, Long targetUserId) {
        if (authentication == null || !authentication.isAuthenticated() || targetUserId == null) {
            return false;
        }
        if (hasRole(authentication, "ADMIN")) {
            return true;
        }
        return authentication.getPrincipal() instanceof AuthenticatedUser user
                && targetUserId.equals(user.userId());
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}