package com.deepan.orderpay.auth.controller;

import com.deepan.orderpay.auth.dto.UpdateProfileRequest;
import com.deepan.orderpay.auth.dto.UserResponse;
import com.deepan.orderpay.auth.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementService userManagementService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.getUser(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    }
}
