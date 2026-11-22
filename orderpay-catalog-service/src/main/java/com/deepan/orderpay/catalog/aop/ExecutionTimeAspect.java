package com.deepan.orderpay.catalog.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ExecutionTimeAspect {

    private final long slowThresholdMs;

    public ExecutionTimeAspect(@Value("${app.aop.slow-threshold-ms:150}") long slowThresholdMs) {
        this.slowThresholdMs = slowThresholdMs;
    }

    @Around("com.deepan.orderpay.catalog.aop.Pointcuts.serviceLayer()")
    public Object time(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        try {
            return pjp.proceed();                       // you MUST call it, and MUST return its result
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (ms >= slowThresholdMs) {
                log.warn("SLOW {} took {} ms", pjp.getSignature().toShortString(), ms);
            } else {
                log.debug("{} took {} ms", pjp.getSignature().toShortString(), ms);
            }
        }
    }
}