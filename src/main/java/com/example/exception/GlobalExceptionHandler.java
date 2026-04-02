package com.example.exception;

import com.example.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServiceExecutionException.class)
    public ResponseEntity<ErrorResponse> handleServiceExecutionException(ServiceExecutionException ex) {
        Throwable cause = ex.getCause();
        int status;
        String code;
        String message;

        if (cause instanceof UserNotFoundException) {
            status = HttpStatus.NOT_FOUND.value();
            code = "USER_NOT_FOUND";
            message = cause.getMessage();
            LOG.error("Ошибка {} ({}): {}", status, code, message);
        } else if (cause instanceof SlotNotFoundException) {
            status = HttpStatus.NOT_FOUND.value();
            code = "SLOT_NOT_FOUND";
            message = cause.getMessage();
            LOG.error("Ошибка {} ({}): {}", status, code, message);
        } else if (cause instanceof SlotAlreadyOccupiedException) {
            status = HttpStatus.CONFLICT.value();
            code = "SLOT_ALREADY_OCCUPIED";
            message = cause.getMessage();
            LOG.error("Ошибка {} ({}): {}", status, code, message);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            code = "INTERNAL_SERVER_ERROR";
            message = "Внутренняя ошибка сервера";
            LOG.error("Ошибка {} ({}): {}", status, code, message, ex);
        }

        ErrorResponse error = new ErrorResponse(status, code, message, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.valueOf(status));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        int status = HttpStatus.NOT_FOUND.value();
        String code = "USER_NOT_FOUND";
        LOG.error("Ошибка {} ({}): {}", status, code, ex.getMessage());
        ErrorResponse error = new ErrorResponse(status, code, ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SlotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSlotNotFound(SlotNotFoundException ex) {
        int status = HttpStatus.NOT_FOUND.value();
        String code = "SLOT_NOT_FOUND";
        LOG.error("Ошибка {} ({}): {}", status, code, ex.getMessage());
        ErrorResponse error = new ErrorResponse(status, code, ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SlotAlreadyOccupiedException.class)
    public ResponseEntity<ErrorResponse> handleSlotAlreadyOccupied(SlotAlreadyOccupiedException ex) {
        int status = HttpStatus.CONFLICT.value();
        String code = "SLOT_ALREADY_OCCUPIED";
        LOG.error("Ошибка {} ({}): {}", status, code, ex.getMessage());
        ErrorResponse error = new ErrorResponse(status, code, ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        int status = HttpStatus.BAD_REQUEST.value();
        String code = "VALIDATION_ERROR";
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        LOG.error("Ошибка {} ({}): {}", status, code, errors.toString());
        ErrorResponse error = new ErrorResponse(status, code, "Ошибка валидации: " + errors.toString(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String code = "INTERNAL_SERVER_ERROR";
        LOG.error("Ошибка {} ({}): {}", status, code, ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(status, code, "Внутренняя ошибка сервера", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}