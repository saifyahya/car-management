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
    Optional<UserAccount> findByUsername(String username);

    @EntityGraph(attributePaths = {"client"})
    Optional<UserAccount> findFirstByUsername(String username);

    @EntityGraph(attributePaths = {"client"})
    List<UserAccount> findAllByUsername(String username);

    @EntityGraph(attributePaths = {"client"})
    Optional<UserAccount> findByUsernameAndClientId(String username, Long clientId);

    @EntityGraph(attributePaths = {"client"})
    List<UserAccount> findAllByClientId(Long clientId);

    @EntityGraph(attributePaths = {"client"})
    Optional<UserAccount> findByIdAndClientId(Long id, Long clientId);
}
