package com.deepan.orderpay.catalog.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Pointcuts {

    @Pointcut("execution(public * com.deepan.orderpay.catalog.service..*.*(..)) "
            + "|| execution(public * com.deepan.orderpay.catalog.report..*.*(..))")
    public void serviceLayer() {}
}