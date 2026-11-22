package com.deepan.orderpay.auth.service;

import com.deepan.orderpay.auth.entity.RefreshToken;
import com.deepan.orderpay.auth.entity.User;
import com.deepan.orderpay.auth.exception.InvalidRefreshTokenException;
import com.deepan.orderpay.auth.repository.RefreshTokenRepository;
import com.deepan.orderpay.auth.repository.UserRepository;
import com.deepan.orderpay.auth.security.RefreshTokenProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    public record Rotation(User user, String refreshToken) {

        @Override
        public String toString() {
            return "Rotation[user=" + user.getEmail()
                    + ", refreshToken=***]";
        }
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;
    private final RefreshTokenProperties properties;
    private final Clock clock;

    @Transactional
    public String startFamily(User user) {
        return issue(user, UUID.randomUUID().toString());
    }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public Rotation rotate(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }

        Instant now = clock.instant();

        RefreshToken current = repository
                .findByTokenHash(hash(rawToken.trim()))
                .orElseThrow(InvalidRefreshTokenException::new);

        // Read these values before bulk updates clear the persistence context
        Long id = current.getId();
        Long userId = current.getUser().getId();
        String familyId = current.getFamilyId();

        if (current.getRevokedAt() != null) {
            throw new InvalidRefreshTokenException();
        }

        if (current.getUsedAt() != null) {
            reuseDetected(familyId, userId, now);
            throw new InvalidRefreshTokenException();
        }

        if (!now.isBefore(current.getExpiresAt())) {
            throw new InvalidRefreshTokenException();
        }

        if (repository.markUsed(id, now) != 1) {
            reuseDetected(familyId, userId, now);
            throw new InvalidRefreshTokenException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!user.isEnabled()) {
            repository.revokeFamily(familyId, now);
            throw new InvalidRefreshTokenException();
        }

        return new Rotation(user, issue(user, familyId));
    }

    @Transactional
    public void revoke(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        repository.findByTokenHash(hash(rawToken.trim()))
                .ifPresent(token ->
                        repository.revokeFamily(
                                token.getFamilyId(),
                                clock.instant()
                        )
                );
    }

    private void reuseDetected(
            String familyId,
            Long userId,
            Instant now
    ) {
        int revoked = repository.revokeFamily(familyId, now);

        log.warn(
                "Refresh token reuse detected for user {}: revoked {} token(s) of the family",
                userId,
                revoked
        );
    }

    private String issue(User user, String familyId) {

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        String raw = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        repository.save(
                new RefreshToken(
                        user,
                        familyId,
                        hash(raw),
                        clock.instant().plus(properties.ttl())
                )
        );

        return raw;
    }

    public static String hash(String rawToken) {

        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(rawToken.getBytes(StandardCharsets.UTF_8))
            );

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 is not available",
                    e
            );
        }
    }
}
