package com.example.valet.controller;

import com.example.valet.dto.ChangePasswordRequest;
import com.example.valet.dto.CreateUserRequest;
import com.example.valet.dto.UpdateUserRequest;
import com.example.valet.dto.UserAccountResponse;
import com.example.valet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserAccountResponse>> list() {
        return ResponseEntity.ok(userService.listUsersForCurrentClient());
    }

    @PostMapping
    public ResponseEntity<UserAccountResponse> create(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userService.createUserForCurrentClient(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(userService.updateUserForCurrentClient(id, req));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<UserAccountResponse> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest req) {
        return ResponseEntity.ok(userService.changeUserPassword(id, req));
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<UserAccountResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserActiveStatus(id));
    }
}
