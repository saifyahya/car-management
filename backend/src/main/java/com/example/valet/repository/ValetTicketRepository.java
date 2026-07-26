package com.example.valet.repository;
import com.example.valet.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ValetTicketRepository extends JpaRepository<ValetTicket,Long> {
  Optional<ValetTicket> findByPublicToken(String token);
  List<ValetTicket> findAllByOrderByCheckedInAtDesc();
  long countByStatus(TicketStatus status);
}