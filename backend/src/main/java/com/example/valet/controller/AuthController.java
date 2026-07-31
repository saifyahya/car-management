package com.example.valet.controller;

import com.example.valet.dto.AuthRequest;
import com.example.valet.dto.AuthResponse;
import com.example.valet.security.JwtUtils;
import com.example.valet.security.SecurityUtils;
import com.example.valet.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtils.generateToken(principal);

        return ResponseEntity.ok(new AuthResponse(
                token,
                principal.getUsername(),
                principal.getRole().name(),
                principal.getClientId(),
                principal.getClientName()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        UserPrincipal principal = SecurityUtils.getCurrentUser();

        return ResponseEntity.ok(new AuthResponse(
                null,
                principal.getUsername(),
                principal.getRole().name(),
                principal.getClientId(),
                principal.getClientName()
        ));
    }
}
