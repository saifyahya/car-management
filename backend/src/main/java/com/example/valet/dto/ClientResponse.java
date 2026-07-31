package com.example.valet.dto;

import com.example.valet.entity.Client;

import java.time.Instant;

public record ClientResponse(
        Long id,
        String name,
        String phoneNumber,
        String email,
        String location,
        Boolean isActive,
        Instant createdAt
) {
    public static ClientResponse from(Client c) {
        return new ClientResponse(
                c.getId(),
                c.getName(),
                c.getPhoneNumber(),
                c.getEmail(),
                c.getLocation(),
                c.getIsActive(),
                c.getCreatedAt()
        );
    }
}
