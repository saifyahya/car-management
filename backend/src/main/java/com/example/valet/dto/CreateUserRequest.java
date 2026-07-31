package com.example.valet.dto;

import com.example.valet.entity.Role;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        Role role
) {}
