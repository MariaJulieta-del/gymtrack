package com.example.gymtrack_backend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maneja errores de validación (@Valid) y los devuelve con mensajes legibles.
 * Ayuda a diagnosticar qué campo falló la validación.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Valor inválido",
                (a, b) -> a
            ));

        System.err.println("[GlobalExceptionHandler] Validación fallida: " + fieldErrors);

        return ResponseEntity.badRequest().body(Map.of(
            "error", "Validación fallida",
            "campos", fieldErrors
        ));
    }
}
