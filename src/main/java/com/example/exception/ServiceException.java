package com.example.exception;

public class ServiceException extends RuntimeException {

    private final String className;
    private final String methodName;
    private final long executionTime;

    public ServiceException(String className, String methodName, long executionTime, Throwable cause) {
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