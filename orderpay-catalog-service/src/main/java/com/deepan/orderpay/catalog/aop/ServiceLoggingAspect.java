package com.deepan.orderpay.catalog.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 30)
public class ServiceLoggingAspect {

    private static final String SERVICE_LAYER = "com.deepan.orderpay.catalog.aop.Pointcuts.serviceLayer()";

    @Before(SERVICE_LAYER)
    public void logCall(JoinPoint jp) {
        log.debug("-> {} args={}", jp.getSignature().toShortString(), ArgSummary.of(jp.getArgs()));
    }

    @AfterReturning(pointcut = SERVICE_LAYER, returning = "result")
    public void logReturn(JoinPoint jp, Object result) {
        log.debug("<- {} returned {}", jp.getSignature().toShortString(),
                result == null ? "null" : result.getClass().getSimpleName());
    }

        @AfterThrowing(pointcut = SERVICE_LAYER, throwing = "ex")
    public void logFailure(JoinPoint jp, Throwable ex) {
        log.debug("!! {} threw {}: {}", jp.getSignature().toShortString(),
                ex.getClass().getSimpleName(), ex.getMessage());
    }
}