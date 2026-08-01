package com.example.valet.repository;

import com.example.valet.entity.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    @EntityGraph(attributePaths = {"client"})
    Optional<UserAccount> findByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = {"client"})
    Optional<UserAccount> findFirstByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = {"client"})
    List<UserAccount> findAllByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = {"client"})
    Optional<UserAccount> findByUsernameIgnoreCaseAndClientId(String username, Long clientId);

    @EntityGraph(attributePaths = {"client"})
    List<UserAccount> findAllByClientId(Long clientId);

    @EntityGraph(attributePaths = {"client"})
    Optional<UserAccount> findByIdAndClientId(Long id, Long clientId);
}
