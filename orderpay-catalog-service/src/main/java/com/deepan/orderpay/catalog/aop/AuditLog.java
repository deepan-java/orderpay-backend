package com.deepan.orderpay.catalog.aop;

import com.deepan.orderpay.catalog.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false, length = 200)
    private String method;

    @Column(length = 250)
    private String arguments;

    @Column(nullable = false, length = 10)
    private String outcome;

    @Column(name = "error_message", length = 250)
    private String errorMessage;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(nullable = false, length = 100)
    private String username;

    public AuditLog(String action, String method, String arguments, String outcome,
                    String errorMessage, long durationMs, String username) {
        this.action = action;
        this.method = method;
        this.arguments = arguments;
        this.outcome = outcome;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
        this.username = username;
    }
}