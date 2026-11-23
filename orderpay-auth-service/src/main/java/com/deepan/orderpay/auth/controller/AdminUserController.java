package com.deepan.orderpay.auth.controller;

import com.deepan.orderpay.auth.dto.UpdateProfileRequest;
import com.deepan.orderpay.auth.dto.UserResponse;
import com.deepan.orderpay.auth.entity.Role;
import com.deepan.orderpay.auth.security.AuthenticatedUser;
import com.deepan.orderpay.auth.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.deepan.orderpay.auth.dto.PageResponse;
import com.deepan.orderpay.auth.dto.UserResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> list(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userManagementService.listUsers(pageable));
    }

    @PutMapping("/{id}/roles/{role}")
    public ResponseEntity<UserResponse> grant(@PathVariable Long id, @PathVariable Role role) {
        return ResponseEntity.ok(userManagementService.grantRole(id, role));
    }

    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<UserResponse> revoke(@PathVariable Long id, @PathVariable Role role,
                                               @AuthenticationPrincipal AuthenticatedUser admin) {
        return ResponseEntity.ok(userManagementService.revokeRole(id, role, admin.userId()));
    }
}
