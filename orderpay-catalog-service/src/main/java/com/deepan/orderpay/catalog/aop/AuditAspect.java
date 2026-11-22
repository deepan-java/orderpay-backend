package com.deepan.orderpay.catalog.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class AuditAspect {

    private static final Logger AUDIT = LoggerFactory.getLogger("AUDIT");

    private final AuditRecorder recorder;

    @Around("@annotation(audit)")
    public Object audit(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
        long start = System.nanoTime();
        String outcome = "SUCCESS";
        String error = null;
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            outcome = "FAILURE";
            error = ArgSummary.truncate(t.getClass().getSimpleName() + ": " + t.getMessage(), 250);
            throw t;                                        // never swallow the business exception
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            String method = pjp.getSignature().getDeclaringType().getSimpleName()
                    + "." + pjp.getSignature().getName();
            String args = ArgSummary.of(pjp.getArgs());
            String user = currentUser();

            AUDIT.info("action={} method={} outcome={} user={} durationMs={} args={}",
                    audit.action(), method, outcome, user, ms, args);
            try {
                recorder.record(new AuditLog(audit.action(), method, args, outcome, error, ms, user));
            } catch (Exception e) {
                log.warn("Could not store the audit record for {}", audit.action(), e);   // auditing must not break the call
            }
        }
    }

    private String currentUser() {
        return "anonymous";        // week 5: read it from the Spring Security context
    }
}