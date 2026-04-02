package com.example.exception;

import com.example.dto.ErrorResponse;
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
        } else if (cause instanceof SlotNotFoundException) {
            status = HttpStatus.NOT_FOUND.value();
            code = "SLOT_NOT_FOUND";
            message = cause.getMessage();
        } else if (cause instanceof SlotAlreadyOccupiedException) {
            status = HttpStatus.CONFLICT.value();
            code = "SLOT_ALREADY_OCCUPIED";
            message = cause.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            code = "INTERNAL_SERVER_ERROR";
            message = "Внутренняя ошибка сервера";
        }

        ErrorResponse error = new ErrorResponse(status, code, message, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.valueOf(status));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "USER_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SlotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSlotNotFound(SlotNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "SLOT_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SlotAlreadyOccupiedException.class)
    public ResponseEntity<ErrorResponse> handleSlotAlreadyOccupied(SlotAlreadyOccupiedException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "SLOT_ALREADY_OCCUPIED",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "Ошибка валидации: " + errors.toString(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "Внутренняя ошибка сервера",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}