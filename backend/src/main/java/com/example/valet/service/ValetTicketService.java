package com.example.valet.service;

import com.example.valet.dto.AssignTicketRequest;
import com.example.valet.dto.CreateTicketRequest;
import com.example.valet.dto.DashboardResponse;
import com.example.valet.dto.TicketResponse;
import com.example.valet.entity.TicketStatus;
import com.example.valet.entity.ValetTicket;
import com.example.valet.repository.ValetTicketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class ValetTicketService {
    private final ValetTicketRepository repository;
    private final SmsGateway smsGateway;
    private final SecureRandom random = new SecureRandom();
    @Value("${app.public-base-url:http://localhost:4200}")
    private String publicBaseUrl;

    public ValetTicketService(ValetTicketRepository repository, SmsGateway smsGateway) {
        this.repository = repository;
        this.smsGateway = smsGateway;
    }

    @Transactional
    public TicketResponse create(CreateTicketRequest r) {
        ValetTicket t = new ValetTicket();
        t.setTicketNumber("VLT-" + (100000 + random.nextInt(900000)));
        t.setPublicToken(UUID.randomUUID().toString().replace("-", ""));
        t.setPickupPin(String.format("%06d", random.nextInt(1_000_000)));
        t.setVisitorPhone(r.visitorPhone());
        t.setPlateNumber(r.plateNumber().toUpperCase());
        t.setMake(r.make());
        t.setModel(r.model());
        t.setColor(r.color());
        t.setParkingLocation(r.parkingLocation());
        t.setKeyLocation(r.keyLocation());
        t.setNotes(r.notes());
        t.setStatus(TicketStatus.PARKED);
        t.setCheckedInAt(Instant.now());
        repository.save(t);
        smsGateway.send(t.getVisitorPhone(), "Welcome. Your valet ticket is " + t.getTicketNumber() + ". Request your vehicle: " + publicBaseUrl + "/v/" + t.getPublicToken() + " Pickup PIN: " + t.getPickupPin());
        return TicketResponse.from(t);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> list() {
        return repository.findAllByOrderByCheckedInAtDesc().stream().map(TicketResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse get(Long id) {
        return TicketResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public TicketResponse getPublic(String token) {
        return TicketResponse.from(repository.findByPublicToken(token).orElseThrow(() -> new NoSuchElementException("Ticket not found")));
    }

    @Transactional
    public TicketResponse request(String token) {
        ValetTicket t = repository.findByPublicToken(token).orElseThrow();
        if (t.getStatus() == TicketStatus.PARKED) {
            t.setStatus(TicketStatus.REQUESTED);
            t.setRequestedAt(Instant.now());
        }
        return TicketResponse.from(t);
    }

    @Transactional
    public TicketResponse assign(Long id, AssignTicketRequest r) {
        ValetTicket t = find(id);
        if (t.getStatus() != TicketStatus.REQUESTED)
            throw new IllegalStateException("Only requested tickets can be assigned");
        t.setAssignedTo(r.assignedTo());
        t.setStatus(TicketStatus.ASSIGNED);
        return TicketResponse.from(t);
    }

    @Transactional
    public TicketResponse transition(Long id, TicketStatus status) {
        ValetTicket t = find(id);
        validateTransition(t.getStatus(), status);
        t.setStatus(status);
        if (status == TicketStatus.READY) {
            t.setReadyAt(Instant.now());
            smsGateway.send(t.getVisitorPhone(), "Your vehicle is ready. Ticket " + t.getTicketNumber() + ". Pickup PIN: " + t.getPickupPin());
        }
        if (status == TicketStatus.DELIVERED) t.setDeliveredAt(Instant.now());
        return TicketResponse.from(t);
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        long parked = repository.countByStatus(TicketStatus.PARKED), requested = repository.countByStatus(TicketStatus.REQUESTED), retrieving = repository.countByStatus(TicketStatus.RETRIEVING), ready = repository.countByStatus(TicketStatus.READY), delivered = repository.countByStatus(TicketStatus.DELIVERED);
        return new DashboardResponse(parked + requested + retrieving + ready, parked, requested, retrieving, ready, delivered);
    }

    private ValetTicket find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Ticket not found"));
    }

    private void validateTransition(TicketStatus from, TicketStatus to) {
        Map<TicketStatus, Set<TicketStatus>> allowed = Map.of(TicketStatus.ASSIGNED, Set.of(TicketStatus.RETRIEVING), TicketStatus.RETRIEVING, Set.of(TicketStatus.READY), TicketStatus.READY, Set.of(TicketStatus.DELIVERED), TicketStatus.PARKED, Set.of(TicketStatus.CANCELLED), TicketStatus.REQUESTED, Set.of(TicketStatus.CANCELLED));
        if (!allowed.getOrDefault(from, Set.of()).contains(to))
            throw new IllegalStateException("Invalid status transition from " + from + " to " + to);
    }
}