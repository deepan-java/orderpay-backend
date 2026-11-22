
package com.deepan.orderpay.auth.repository;

import com.deepan.orderpay.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    long countByFamilyIdAndRevokedAtIsNull(String familyId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update RefreshToken t
        set t.usedAt = :now
        where t.id = :id
        and t.usedAt is null
        and t.revokedAt is null
        """)
    int markUsed(
            @Param("id") Long id,
            @Param("now") Instant now
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update RefreshToken t
        set t.revokedAt = :now
        where t.familyId = :familyId
        and t.revokedAt is null
        """)
    int revokeFamily(
            @Param("familyId") String familyId,
            @Param("now") Instant now
    );
}
