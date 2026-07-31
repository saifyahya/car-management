package com.example.valet.repository;

import com.example.valet.entity.TicketStatus;
import com.example.valet.entity.ValetTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValetTicketRepository extends JpaRepository<ValetTicket, Long> {
    Optional<ValetTicket> findByPublicToken(String token);

    List<ValetTicket> findAllByClientIdOrderByCheckedInAtDesc(Long clientId);

    Page<ValetTicket> findAllByClientId(Long clientId, Pageable pageable);

    Page<ValetTicket> findAllByClientIdAndStatus(Long clientId, TicketStatus status, Pageable pageable);

    Page<ValetTicket> findAllByClientIdAndCreatedBy(Long clientId, String createdBy, Pageable pageable);

    Page<ValetTicket> findAllByClientIdAndCreatedByAndStatus(Long clientId, String createdBy, TicketStatus status, Pageable pageable);

    Optional<ValetTicket> findByIdAndClientId(Long id, Long clientId);

    long countByClientIdAndStatus(Long clientId, TicketStatus status);

    long countByClientIdAndCreatedByAndStatus(Long clientId, String createdBy, TicketStatus status);
}