package com.example.valet.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
        @NotBlank String name,
        String phoneNumber,
        String email,
        String location,
        Boolean isActive,
        String username,
        String defaultPassword
) {}
