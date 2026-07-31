package com.example.valet.service;

import com.example.valet.dto.ChangePasswordRequest;
import com.example.valet.dto.CreateUserRequest;
import com.example.valet.dto.UpdateUserRequest;
import com.example.valet.dto.UserAccountResponse;
import com.example.valet.entity.Client;
import com.example.valet.entity.Role;
import com.example.valet.entity.UserAccount;
import com.example.valet.repository.ClientRepository;
import com.example.valet.repository.UserRepository;
import com.example.valet.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void validatePasswordStrength(String password) {
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one number and one special character.");
        }
    }

    @Transactional(readOnly = true)
    public List<UserAccountResponse> listUsersForCurrentClient() {
        Long clientId = SecurityUtils.getCurrentClientId();
        return userRepository.findAllByClientId(clientId).stream().map(UserAccountResponse::from).toList();
    }

    @Transactional
    public UserAccountResponse createUserForCurrentClient(CreateUserRequest req) {
        Long clientId = SecurityUtils.getCurrentClientId();
        String currentUsername = SecurityUtils.getCurrentUsername();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + clientId));

        userRepository.findByUsernameAndClientId(req.username().trim(), clientId).ifPresent(u -> {
            throw new IllegalArgumentException("Username '" + req.username() + "' already exists for this client");
        });

        validatePasswordStrength(req.password());

        UserAccount user = new UserAccount();
        user.setUsername(req.username().trim());
        user.setPassword(passwordEncoder.encode(req.password().trim()));
        user.setRole(req.role() != null ? req.role() : Role.VALET);
        user.setClient(client);
        user.setIsActive(true);
        user.setCreatedBy(currentUsername);
        user.setUpdatedBy(currentUsername);
        userRepository.save(user);

        return UserAccountResponse.from(user);
    }

    @Transactional
    public UserAccountResponse updateUserForCurrentClient(Long userId, UpdateUserRequest req) {
        Long tokenClientId = SecurityUtils.getCurrentClientId();
        String currentUsername = SecurityUtils.getCurrentUsername();
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        if (user.getClient() == null || !user.getClient().getId().equals(tokenClientId)) {
            throw new IllegalStateException("Unauthorized: User account client ID does not match token client ID (" + tokenClientId + ")");
        }

        String newUsername = req.username().trim();
        if (!user.getUsername().equalsIgnoreCase(newUsername)) {
            userRepository.findByUsernameAndClientId(newUsername, tokenClientId).ifPresent(u -> {
                throw new IllegalArgumentException("Username '" + newUsername + "' is already taken for this client");
            });
            user.setUsername(newUsername);
        }

        if (req.role() != null) {
            user.setRole(req.role());
        }

        user.setUpdatedBy(currentUsername);
        userRepository.save(user);
        return UserAccountResponse.from(user);
    }

    @Transactional
    public UserAccountResponse changeUserPassword(Long userId, ChangePasswordRequest req) {
        Long tokenClientId = SecurityUtils.getCurrentClientId();
        String currentUsername = SecurityUtils.getCurrentUsername();
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        if (user.getClient() == null || !user.getClient().getId().equals(tokenClientId)) {
            throw new IllegalStateException("Unauthorized: User account client ID does not match token client ID (" + tokenClientId + ")");
        }

        if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        validatePasswordStrength(req.newPassword().trim());

        user.setPassword(passwordEncoder.encode(req.newPassword().trim()));
        user.setUpdatedBy(currentUsername);
        userRepository.save(user);

        return UserAccountResponse.from(user);
    }

    @Transactional
    public UserAccountResponse toggleUserActiveStatus(Long userId) {
        Long tokenClientId = SecurityUtils.getCurrentClientId();
        String currentUsername = SecurityUtils.getCurrentUsername();
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        if (user.getClient() == null || !user.getClient().getId().equals(tokenClientId)) {
            throw new IllegalStateException("Unauthorized: User account client ID does not match token client ID (" + tokenClientId + ")");
        }

        user.setIsActive(!user.getIsActive());
        user.setUpdatedBy(currentUsername);
        userRepository.save(user);

        return UserAccountResponse.from(user);
    }
}
