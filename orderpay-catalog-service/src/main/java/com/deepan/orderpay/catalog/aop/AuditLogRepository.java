package com.deepan.orderpay.catalog.aop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    long countByActionAndOutcome(String action, String outcome);

    @Transactional
    long deleteByCreatedAtGreaterThanEqual(Instant from);
}