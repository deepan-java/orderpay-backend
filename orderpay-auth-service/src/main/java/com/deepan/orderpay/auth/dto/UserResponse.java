package com.deepan.orderpay.auth.dto;

import com.deepan.orderpay.auth.entity.Role;
import com.deepan.orderpay.auth.entity.User;

import java.time.Instant;
import java.util.Set;

public record UserResponse(Long id, String email, String fullName, Set<Role> roles, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(),
                Set.copyOf(user.getRoles()), user.getCreatedAt());
    }
}