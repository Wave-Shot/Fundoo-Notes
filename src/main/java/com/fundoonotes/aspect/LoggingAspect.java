package com.fundoonotes.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect         // Tells Spring: this class contains AOP logic
@Component      // Tells Spring: register this as a bean
public class LoggingAspect {

    // One logger for this aspect class itself
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /*
     * POINTCUT DEFINITION
     * This expression means: "target every method inside any class
     * that lives in com.fundoonotes.service or its sub-packages,
     * with any return type and any arguments"
     * The && !get* part skips simple getter methods to reduce noise
     */
    @Pointcut("execution(* com.fundoonotes.service..*(..))")
    public void serviceLayer() {
        // This method body stays empty.
        // It only exists as a named reference for the pointcut expression above.
    }

    /*
     * BEFORE ADVICE
     * Runs just before any matched service method executes.
     * JoinPoint gives you info about the method being called.
     */
    @Before("serviceLayer()")
    public void logMethodEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info(">>> Entering: {} | Args: {}", methodName, Arrays.toString(args));
    }

    /*
     * AFTER RETURNING ADVICE
     * Runs only when the method returns successfully (no exception).
     * 'returning = "result"' binds the actual return value to the parameter below.
     */
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("<<< Exiting: {} | Returned: {}", methodName, result);
    }

    /*
     * AFTER THROWING ADVICE
     * Runs only when the method throws an exception.
     * 'throwing = "ex"' binds the actual exception to the parameter below.
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("!!! Exception in: {} | Message: {}", methodName, ex.getMessage());
    }

    /*
     * AROUND ADVICE — the most powerful type
     * ProceedingJoinPoint is like JoinPoint but with one extra power:
     * you must call joinPoint.proceed() to actually run the real method.
     * Everything before proceed() = "before". Everything after = "after".
     * This lets us measure execution time.
     */
    @Around("serviceLayer()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // This line actually calls the real service method
        Object result = joinPoint.proceed();

        long timeTaken = System.currentTimeMillis() - startTime;
        log.debug("⏱ {} executed in {} ms",
                joinPoint.getSignature().toShortString(), timeTaken);

        // You MUST return the result, otherwise the caller gets null
        return result;
    }
}