package com.example.valet.service;

import com.example.valet.dto.ClientResponse;
import com.example.valet.dto.CreateClientRequest;
import com.example.valet.dto.UpdateClientRequest;
import com.example.valet.entity.Client;
import com.example.valet.entity.Role;
import com.example.valet.entity.UserAccount;
import com.example.valet.repository.ClientRepository;
import com.example.valet.repository.UserRepository;
import com.example.valet.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClientResponse create(CreateClientRequest r) {
        String currentUsername = SecurityUtils.getCurrentUsername();

        Client c = new Client();
        c.setName(r.name());
        c.setPhoneNumber(r.phoneNumber());
        c.setEmail(r.email());
        c.setLocation(r.location());
        c.setIsActive(r.isActive() != null ? r.isActive() : true);
        c.setCreatedAt(Instant.now());
        c.setCreatedBy(currentUsername);
        c.setUpdatedBy(currentUsername);
        clientRepository.save(c);

        if (r.username() != null && !r.username().isBlank()) {
            UserAccount user = new UserAccount();
            user.setUsername(r.username().trim());
            String rawPassword = (r.defaultPassword() != null && !r.defaultPassword().isBlank()) ? r.defaultPassword().trim() : "valet123";
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(Role.MANAGER);
            user.setClient(c);
            user.setIsActive(c.getIsActive());
            user.setCreatedBy(currentUsername);
            user.setUpdatedBy(currentUsername);
            userRepository.save(user);
        }

        return ClientResponse.from(c);
    }

    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest r) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + id));
        c.setName(r.name());
        c.setPhoneNumber(r.phoneNumber());
        c.setEmail(r.email());
        c.setLocation(r.location());
        if (r.isActive() != null) {
            c.setIsActive(r.isActive());
        }
        c.setUpdatedAt(Instant.now());
        c.setUpdatedBy(currentUsername);
        return ClientResponse.from(c);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> list() {
        return clientRepository.findAll().stream().map(ClientResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ClientResponse get(Long id) {
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + id));
        return ClientResponse.from(c);
    }
}
