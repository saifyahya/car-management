package com.example.valet.config;

import com.example.valet.entity.Role;
import com.example.valet.entity.UserAccount;
import com.example.valet.repository.ClientRepository;
import com.example.valet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ClientRepository clientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (clientRepository.count() == 0) {

            if (userRepository.findByUsername("admin").isEmpty()) {

                log.info("Seeding default ADMIN and VALET staff user accounts...");
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setIsActive(true);
                userRepository.save(admin);

            }
        }
    }
}
