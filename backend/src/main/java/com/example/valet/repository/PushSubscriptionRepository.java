package com.example.valet.repository;

import com.example.valet.entity.PushSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscriptionEntity, Long> {
    List<PushSubscriptionEntity> findByClientId(Long clientId);
    Optional<PushSubscriptionEntity> findByEndpoint(String endpoint);
    void deleteByEndpoint(String endpoint);
}
