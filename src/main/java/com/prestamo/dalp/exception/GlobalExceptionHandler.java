package com.prestamo.dalp.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", error);
        errorDetails.put("message", message);
        errorDetails.put("path", request.getRequestURI());
        return errorDetails;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        Map<String, Object> errorDetails = buildErrorResponse(HttpStatus.BAD_REQUEST, "Error de validación", e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        Map<String, Object> errorDetails = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado", e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e, HttpServletRequest request) {
        Map<String, Object> errorDetails = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}
