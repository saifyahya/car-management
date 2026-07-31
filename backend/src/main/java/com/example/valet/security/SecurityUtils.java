package com.example.valet.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        throw new IllegalStateException("No authenticated user found in security context");
    }

    public static Long getCurrentClientId() {
        return getCurrentUser().getClientId();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
}
