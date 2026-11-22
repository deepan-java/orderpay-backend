package com.deepan.orderpay.auth.security;

import com.deepan.orderpay.auth.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    private static final Duration CLOCK_SKEW = Duration.ofSeconds(30);

    private final byte[] secret;
    private final String issuer;
    private final Duration accessTokenTtl;
    private final Clock clock;

    public JwtService(JwtProperties properties, Clock clock) {
        this.secret = decodeSecret(properties.secret());
        this.issuer = properties.issuer();
        this.accessTokenTtl = properties.accessTokenTtl();
        this.clock = clock;
    }

    private static byte[] decodeSecret(String base64) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("app.security.jwt.secret must be valid Base64", e);
        }
        if (bytes.length < 32) {
            throw new IllegalStateException("app.security.jwt.secret must decode to at least 32 bytes (256 bits)");
        }
        return bytes;
    }

    public IssuedToken issueAccessToken(User user) {
        Instant now = clock.instant();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(String.valueOf(user.getId()))
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plus(accessTokenTtl)))
                .jwtID(UUID.randomUUID().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(Enum::name).sorted().toList())
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(), claims);
        try {
            jwt.sign(new MACSigner(secret));
        } catch (JOSEException e) {
            throw new IllegalStateException("Could not sign the token", e);
        }
        return new IssuedToken(jwt.serialize(), accessTokenTtl.toSeconds());
    }

    public TokenClaims parse(String token) {
        if (token == null || token.isBlank()) {
            throw invalid("empty token");
        }
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())) {     // pin the algorithm: never trust the header
                throw invalid("unexpected algorithm");
            }
            if (!jwt.verify(new MACVerifier(secret))) {
                throw invalid("signature mismatch");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Instant now = clock.instant();

            if (!issuer.equals(claims.getIssuer())) {
                throw invalid("wrong issuer");
            }
            if (claims.getExpirationTime() == null || claims.getSubject() == null) {
                throw invalid("missing required claims");
            }
            Instant expiresAt = claims.getExpirationTime().toInstant();
            if (now.isAfter(expiresAt.plus(CLOCK_SKEW))) {
                throw invalid("expired");
            }
            if (claims.getNotBeforeTime() != null
                    && now.isBefore(claims.getNotBeforeTime().toInstant().minus(CLOCK_SKEW))) {
                throw invalid("not valid yet");
            }

            List<String> roles = claims.getStringListClaim("roles");
            return new TokenClaims(
                    Long.parseLong(claims.getSubject()),
                    claims.getStringClaim("email"),
                    roles == null ? Set.of() : new HashSet<>(roles),
                    claims.getJWTID(),
                    claims.getIssueTime() == null ? null : claims.getIssueTime().toInstant(),
                    expiresAt);
        } catch (ParseException | JOSEException | NumberFormatException e) {
            throw invalid("malformed token");
        }
    }

    private InvalidTokenException invalid(String reason) {
        log.debug("Rejected token: {}", reason);               // the reason is for logs, never for the client
        return new InvalidTokenException("Invalid token");
    }
}