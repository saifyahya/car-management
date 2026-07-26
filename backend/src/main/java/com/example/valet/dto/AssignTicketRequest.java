package com.example.valet.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignTicketRequest(@NotBlank String assignedTo) {
}