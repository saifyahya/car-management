package com.example.valet.security;

import com.example.valet.entity.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Long clientId;
    private final String clientName;
    private final RoleAuthority role;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean active;

    public UserPrincipal(Long id, String username, String password, Long clientId, String clientName, RoleAuthority role, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientName = clientName;
        this.role = role;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
        this.active = active;
    }

    public static UserPrincipal create(UserAccount user) {
        Long clientId = user.getClient() != null ? user.getClient().getId() : null;
        String clientName = user.getClient() != null ? user.getClient().getName() : "System Admin";
        boolean isClientActive = user.getClient() == null || Boolean.TRUE.equals(user.getClient().getIsActive());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                clientId,
                clientName,
                RoleAuthority.valueOf(user.getRole().name()),
                Boolean.TRUE.equals(user.getIsActive()) && isClientActive
        );
    }

    public enum RoleAuthority {
        ADMIN, VALET, MANAGER
    }

    public Long getId() {
        return id;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public RoleAuthority getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
