package com.example.exception;

/**
 * Исключение, выбрасываемое при ошибках в сервисном слое,
 * перехваченных через LoggingAspect.
 */
public class ServiceExecutionException extends RuntimeException {

    private final String className;
    private final String methodName;
    private final long executionTime;

    public ServiceExecutionException(String message, Throwable cause) {
        super(message, cause);
        this.className = null;
        this.methodName = null;
        this.executionTime = 0;
    }

    public ServiceExecutionException(String className, String methodName, long executionTime, Throwable cause) {
        super(String.format("Ошибка при выполнении метода %s.%s() после %d ms: %s",
            className, methodName, executionTime, cause.getMessage()), cause);
        this.className = className;
        this.methodName = methodName;
        this.executionTime = executionTime;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}