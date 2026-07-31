package com.example.valet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
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

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Map<String, Object>> illegalState(IllegalStateException e) {
        if (e.getMessage() != null && e.getMessage().contains("security context")) {
            return body(HttpStatus.UNAUTHORIZED, e);
        }
        return body(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, Object>> bad(IllegalArgumentException e) {
        return body(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({DisabledException.class, LockedException.class})
    ResponseEntity<Map<String, Object>> accountDisabled(Exception e) {
        return body(HttpStatus.FORBIDDEN, new Exception("Your account is not active, please contact support"));
    }

    @ExceptionHandler({org.springframework.security.authentication.BadCredentialsException.class, AuthenticationException.class})
    ResponseEntity<Map<String, Object>> unauthorized(Exception e) {
        return body(HttpStatus.UNAUTHORIZED, new Exception("Invalid username or password"));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");
        return body(HttpStatus.BAD_REQUEST, new Exception(msg));
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus s, Exception e) {
        return ResponseEntity.status(s).body(Map.of("timestamp", Instant.now(), "status", s.value(), "message", e.getMessage()));
    }
}