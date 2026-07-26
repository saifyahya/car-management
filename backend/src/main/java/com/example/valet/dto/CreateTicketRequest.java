package com.example.valet.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTicketRequest(
        @NotBlank String visitorPhone, @NotBlank String plateNumber, @NotBlank String make,
        @NotBlank String model, @NotBlank String color, String parkingLocation, String keyLocation, String notes) {
}