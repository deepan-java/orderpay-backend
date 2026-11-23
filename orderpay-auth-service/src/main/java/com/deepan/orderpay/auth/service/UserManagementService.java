package com.deepan.orderpay.auth.service;

import com.deepan.orderpay.auth.dto.PageResponse;
import com.deepan.orderpay.auth.dto.UpdateProfileRequest;
import com.deepan.orderpay.auth.dto.UserResponse;
import com.deepan.orderpay.auth.entity.Role;
import com.deepan.orderpay.auth.entity.User;
import com.deepan.orderpay.auth.exception.InvalidRequestException;
import com.deepan.orderpay.auth.exception.ResourceNotFoundException;
import com.deepan.orderpay.auth.exception.RoleChangeConflictException;
import com.deepan.orderpay.auth.repository.RefreshTokenRepository;
import com.deepan.orderpay.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserManagementService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    @PreAuthorize("@authz.isSelfOrAdmin(authentication, #id)")
    public UserResponse getUser(Long id) {
        return UserResponse.from(load(id));
    }

    @Transactional
    @PreAuthorize("@authz.isSelfOrAdmin(authentication, #id)")
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = load(id);
        user.updateFullName(request.fullName().trim());
        return UserResponse.from(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> listUsers(Pageable pageable) {
        // the client's sort is ignored: sorting by a sensitive column (say password_hash) would leak information
        Pageable fixed = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
        return PageResponse.from(userRepository.findAll(fixed).map(UserResponse::from));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse grantRole(Long targetId, Role role) {
        requireManageable(role);
        User user = load(targetId);
        user.addRole(role);                                    // adding an existing role changes nothing: idempotent
        return UserResponse.from(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse revokeRole(Long targetId, Role role, Long actingAdminId) {
        requireManageable(role);
        User user = load(targetId);
        if (!user.hasRole(role)) {
            return UserResponse.from(user);                    // already without it: idempotent
        }
        if (targetId.equals(actingAdminId)) {
            throw new RoleChangeConflictException("Admins cannot remove their own ADMIN role");
        }
        if (userRepository.countWithRole(role) <= 1) {
            throw new RoleChangeConflictException("Cannot remove the last ADMIN");
        }
        user.removeRole(role);
        // a demotion must take effect for NEW tokens immediately: kill every session so the next login or
        // refresh is rebuilt from the database (this bulk update flushes the role change first)
        int revoked = refreshTokenRepository.revokeAllForUser(targetId, clock.instant());
        log.info("Role {} revoked from user {}: {} refresh token(s) revoked", role, targetId, revoked);
        return UserResponse.from(user);
    }

    private void requireManageable(Role role) {
        if (role != Role.ADMIN) {
            throw new InvalidRequestException("Only the ADMIN role can be granted or revoked");
        }
    }

    private User load(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}