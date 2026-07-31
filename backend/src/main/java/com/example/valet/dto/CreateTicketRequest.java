package com.example.valet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateTicketRequest(
        @NotBlank(message = "Visitor phone is required")
        @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Phone must be a valid E.164 international format (e.g. +962790824434)")
        String visitorPhone,

        @Email(message = "Invalid email address format")
        String visitorEmail,

        @NotBlank(message = "Plate number is required") String plateNumber,
        @NotBlank(message = "Make is required") String make,
        @NotBlank(message = "Model is required") String model,
        @NotBlank(message = "Color is required") String color,
        @NotBlank(message = "Parking location is required") String parkingLocation,
        @NotBlank(message = "Key location is required") String keyLocation,
        String notes
) {
}