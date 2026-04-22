package com.fundoonotes.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ExceptionLoggingAspect.class);

    // Targets all controller methods
    @Pointcut("execution(* com.fundoonotes.controller..*(..))")
    public void controllerLayer() {}

    @AfterThrowing(pointcut = "controllerLayer()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Throwable ex) {
        log.error("CONTROLLER EXCEPTION in [{}] — {}",
                joinPoint.getSignature().toShortString(),
                ex.getMessage());
    }
}