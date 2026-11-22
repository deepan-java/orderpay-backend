package com.deepan.orderpay.auth.service;

import com.deepan.orderpay.auth.dto.ChangePasswordRequest;
import com.deepan.orderpay.auth.entity.User;
import com.deepan.orderpay.auth.exception.InvalidCredentialsException;
import com.deepan.orderpay.auth.exception.InvalidPasswordChangeException;
import com.deepan.orderpay.auth.repository.RefreshTokenRepository;
import com.deepan.orderpay.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    // Not @Transactional: verifying and hashing passwords takes a few hundred ms of CPU, and a transaction
    // would hold a connection through it. Only the short write below is wrapped, using TransactionTemplate.
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordChangeException("Current password is incorrect");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new InvalidPasswordChangeException("New password must be different from the current password");
        }
        String newHash = passwordEncoder.encode(request.newPassword());

        transactionTemplate.executeWithoutResult(status -> {
            User fresh = userRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);
            fresh.changePassword(newHash);
            // flushAutomatically on the bulk update writes the new hash BEFORE the update runs,
            // and both changes commit together: a new password never exists without the sessions being revoked
            int revoked = refreshTokenRepository.revokeAllForUser(userId, clock.instant());
            log.info("Password changed for user {}: revoked {} refresh token(s)", userId, revoked);
        });
    }

    @Transactional
    public void logoutAll(Long userId) {
        int revoked = refreshTokenRepository.revokeAllForUser(userId, clock.instant());
        log.info("Logged out everywhere for user {}: revoked {} refresh token(s)", userId, revoked);
    }
}