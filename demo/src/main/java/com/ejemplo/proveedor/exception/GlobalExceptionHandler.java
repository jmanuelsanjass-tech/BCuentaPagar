package com.ejemplo.proveedor.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {

        ex.printStackTrace();

        return ResponseEntity.badRequest().body(
            Map.of(
                "error", ex.getClass().getSimpleName(),
                "message", ex.getMessage()
            )
        );
    }
}