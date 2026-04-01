package com.example.aspect;

import com.example.exception.ServiceExecutionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.example.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        LOG.debug("Выполнение метода: {}.{}()", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            LOG.info("Метод {}.{}() выполнен за {} ms", className, methodName, executionTime);
            return result;
        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - start;

            // Логируем ошибку
            LOG.error("Ошибка в методе {}.{}() после {} ms: {}",
                className, methodName, executionTime, e.getMessage(), e);

            // Бросаем специфичное исключение с контекстом
            throw new ServiceExecutionException(className, methodName, executionTime, e);
        }
    }
}