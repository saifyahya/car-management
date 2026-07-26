package com.example.valet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, Object>> notFound(Exception e) {
        return body(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    ResponseEntity<Map<String, Object>> bad(Exception e) {
        return body(HttpStatus.CONFLICT, e);
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus s, Exception e) {
        return ResponseEntity.status(s).body(Map.of("timestamp", Instant.now(), "status", s.value(), "message", e.getMessage()));
    }
}