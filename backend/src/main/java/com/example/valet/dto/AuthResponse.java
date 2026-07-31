package com.example.valet.dto;

public record AuthResponse(
        String token,
        String username,
        String role,
        Long clientId,
        String clientName
) {}
