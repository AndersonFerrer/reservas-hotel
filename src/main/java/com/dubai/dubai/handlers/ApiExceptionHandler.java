package com.dubai.dubai.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> manejarEntityNotFound(EntityNotFoundException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(mensaje));
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", mensaje);
        return body;
    }
}
