package com.atunesdelpacifico.exception;

import com.atunesdelpacifico.model.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.PersistenceException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Errores de validación", errors.toString()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Credenciales inválidas"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Acceso denegado"));
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ApiResponse<String>> handleDataAccessException(
            org.springframework.dao.DataAccessException ex) {
        System.err.println("Error de acceso a datos: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error de base de datos: " + ex.getMessage()));
    }

    @ExceptionHandler(jakarta.persistence.PersistenceException.class)
    public ResponseEntity<ApiResponse<String>> handlePersistenceException(
            jakarta.persistence.PersistenceException ex) {
        System.err.println("Error de persistencia: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error de persistencia: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor: " + ex.getMessage()));
    }
}
