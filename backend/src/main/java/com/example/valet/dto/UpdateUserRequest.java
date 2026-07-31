package com.example.valet.dto;

import com.example.valet.entity.Role;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String username,
        String password,
        Role role
) {}
