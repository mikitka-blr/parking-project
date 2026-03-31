package com.example.exception;

public class ServiceException extends Exception {

    private final String className;
    private final String methodName;
    private final long executionTime;

    /**
     * Конструктор с контекстной информацией о методе и времени выполнения.
     *
     * @param className имя класса, в котором произошла ошибка
     * @param methodName имя метода, в котором произошла ошибка
     * @param executionTime время выполнения до ошибки (в миллисекундах)
     * @param cause исходное исключение
     */
    public ServiceException(String className, String methodName, long executionTime, Throwable cause) {
        super(String.format("Ошибка при выполнении метода %s.%s() после %d ms: %s",
            className, methodName, executionTime, cause.getMessage()), cause);
        this.className = className;
        this.methodName = methodName;
        this.executionTime = executionTime;
    }

    /**
     * Конструктор с произвольным сообщением.
     *
     * @param message сообщение об ошибке
     * @param cause исходное исключение
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.className = null;
        this.methodName = null;
        this.executionTime = 0;
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