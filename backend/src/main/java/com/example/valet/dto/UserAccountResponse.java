package com.example.valet.dto;

import com.example.valet.entity.Role;
import com.example.valet.entity.UserAccount;

public record UserAccountResponse(
        Long id,
        String username,
        Role role,
        Long clientId,
        Boolean isActive
) {
    public static UserAccountResponse from(UserAccount u) {
        return new UserAccountResponse(
                u.getId(),
                u.getUsername(),
                u.getRole(),
                u.getClient() != null ? u.getClient().getId() : null,
                u.getIsActive()
        );
    }
}
