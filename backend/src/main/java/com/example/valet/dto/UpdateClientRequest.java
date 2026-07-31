package com.example.valet.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateClientRequest(
        @NotBlank String name,
        String phoneNumber,
        String email,
        String location,
        Boolean isActive
) {}
