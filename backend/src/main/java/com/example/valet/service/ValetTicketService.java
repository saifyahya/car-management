package com.example.valet.service;

import com.example.valet.dto.AssignTicketRequest;
import com.example.valet.dto.CreateTicketRequest;
import com.example.valet.dto.DashboardResponse;
import com.example.valet.dto.PageResponse;
import com.example.valet.dto.TicketResponse;
import com.example.valet.entity.Client;
import com.example.valet.entity.TicketStatus;
import com.example.valet.entity.ValetTicket;
import com.example.valet.repository.ClientRepository;
import com.example.valet.repository.ValetTicketRepository;
import com.example.valet.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class ValetTicketService {
    private final ValetTicketRepository repository;
    private final ClientRepository clientRepository;
    private final SmsGateway smsGateway;
    private final PushNotificationService pushNotificationService;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.public-base-url:http://localhost:4200}")
    private String publicBaseUrl;

    @Value("${app.sms.provider:mail}")
    private String smsProvider;

    public ValetTicketService(ValetTicketRepository repository, ClientRepository clientRepository, SmsGateway smsGateway, PushNotificationService pushNotificationService) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.smsGateway = smsGateway;
        this.pushNotificationService = pushNotificationService;
    }

    private String getNotificationRecipient(ValetTicket t) {
        if ("mail".equalsIgnoreCase(smsProvider)) {
            return t.getVisitorEmail();
        }
        return t.getVisitorPhone();
    }

    private String buildVisitorRequestUrl(ValetTicket t) {
        String base = (publicBaseUrl != null && !publicBaseUrl.isBlank()) ? publicBaseUrl.trim().replaceAll("/+$", "") : "http://localhost:4200";
        if (!base.startsWith("http://") && !base.startsWith("https://")) {
            base = "http://" + base;
        }
        return base + "/v/" + t.getPublicToken();
    }

    @Transactional(rollbackFor = Exception.class)
    public TicketResponse create(CreateTicketRequest r) {
        Long clientId = SecurityUtils.getCurrentClientId();
        String currentUsername = SecurityUtils.getCurrentUsername();

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client tenant not found for ID: " + clientId));

        ValetTicket t = new ValetTicket();
        t.setClient(client);
        t.setTicketNumber("VLT-" + (100000 + random.nextInt(900000)));
        t.setPublicToken(UUID.randomUUID().toString().replace("-", ""));
        t.setPickupPin(String.format("%06d", random.nextInt(1_000_000)));
        t.setVisitorPhone(r.visitorPhone());
        t.setVisitorEmail(r.visitorEmail());
        t.setPlateNumber(r.plateNumber().toUpperCase());
        t.setMake(r.make());
        t.setModel(r.model());
        t.setColor(r.color());
        t.setParkingLocation(r.parkingLocation());
        t.setKeyLocation(r.keyLocation());
        t.setNotes(r.notes());
        t.setStatus(TicketStatus.PARKED);
        t.setCheckedInAt(Instant.now());
        t.setCreatedBy(currentUsername);
        t.setUpdatedBy(currentUsername);
        repository.save(t);

        String recipient = getNotificationRecipient(t);
        if (recipient != null && !recipient.isBlank()) {
            String requestUrl = buildVisitorRequestUrl(t);
            if (smsGateway instanceof EmailSmsGateway emailGateway) {
                emailGateway.sendTicketCheckInEmail(t, requestUrl);
            } else {
                smsGateway.send(recipient, "Welcome. Your valet ticket is " + t.getTicketNumber() + ". Request your vehicle: " + requestUrl + " Pickup PIN: " + t.getPickupPin());
            }
        }
        return TicketResponse.from(t);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> list() {
        Long clientId = SecurityUtils.getCurrentClientId();
        return repository.findAllByClientIdOrderByCheckedInAtDesc(clientId).stream().map(TicketResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketResponse> listPaginated(int page, int size, String status) {
        Long clientId = SecurityUtils.getCurrentClientId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "checkedInAt"));

        Page<ValetTicket> ticketPage;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            try {
                TicketStatus ticketStatus = TicketStatus.valueOf(status.trim().toUpperCase());
                ticketPage = repository.findAllByClientIdAndStatus(clientId, ticketStatus, pageable);
            } catch (IllegalArgumentException e) {
                ticketPage = repository.findAllByClientId(clientId, pageable);
            }
        } else {
            ticketPage = repository.findAllByClientId(clientId, pageable);
        }

        return PageResponse.from(ticketPage.map(TicketResponse::from));
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
            pushNotificationService.sendVehicleRequestedNotification(t);
        }
        return TicketResponse.from(t);
    }

    @Transactional
    public TicketResponse assign(Long id, AssignTicketRequest r) {
        ValetTicket t = find(id);
        if (t.getStatus() != TicketStatus.REQUESTED)
            throw new IllegalStateException("Only requested tickets can be assigned");
        t.setAssignedTo(r.assignedTo());
        t.setStatus(TicketStatus.RETRIEVING);
        t.setUpdatedBy(SecurityUtils.getCurrentUsername());
        return TicketResponse.from(t);
    }

    @Transactional
    public TicketResponse transition(Long id, TicketStatus status) {
        Long clientId = SecurityUtils.getCurrentClientId();
        ValetTicket t = find(id);
        if (!t.getClient().getId().equals(clientId)) {
            throw new IllegalStateException("Unauthorized: Ticket client ID (" + t.getClient().getId() + ") does not match token client ID (" + clientId + ")");
        }
        validateTransition(t.getStatus(), status);
        t.setStatus(status);
        t.setUpdatedBy(SecurityUtils.getCurrentUsername());
        if (status == TicketStatus.READY) {
            t.setReadyAt(Instant.now());
//            String recipient = getNotificationRecipient(t);
//            if (recipient != null && !recipient.isBlank()) {
//                if (smsGateway instanceof EmailSmsGateway emailGateway) {
//                    emailGateway.sendVehicleReadyEmail(t);
//                } else {
//                    smsGateway.send(recipient, "Your vehicle is ready. Ticket " + t.getTicketNumber() + ". Pickup PIN: " + t.getPickupPin());
//                }
//            }
        }
        if (status == TicketStatus.DELIVERED) t.setDeliveredAt(Instant.now());
        return TicketResponse.from(t);
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        Long clientId = SecurityUtils.getCurrentClientId();
        long parked = repository.countByClientIdAndStatus(clientId, TicketStatus.PARKED);
        long requested = repository.countByClientIdAndStatus(clientId, TicketStatus.REQUESTED);
        long retrieving = repository.countByClientIdAndStatus(clientId, TicketStatus.RETRIEVING);
        long ready = repository.countByClientIdAndStatus(clientId, TicketStatus.READY);
        long delivered = repository.countByClientIdAndStatus(clientId, TicketStatus.DELIVERED);
        return new DashboardResponse(parked + requested + retrieving + ready, parked, requested, retrieving, ready, delivered);
    }

    private ValetTicket find(Long id) {
        Long clientId = SecurityUtils.getCurrentClientId();
        ValetTicket ticket = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with id: " + id));

        if (!ticket.getClient().getId().equals(clientId)) {
            throw new IllegalStateException("Unauthorized: Ticket client ID (" + ticket.getClient().getId() + ") does not match token client ID (" + clientId + ")");
        }
        return ticket;
    }

    private void validateTransition(TicketStatus from, TicketStatus to) {
        Map<TicketStatus, Set<TicketStatus>> allowed = Map.of(TicketStatus.ASSIGNED, Set.of(TicketStatus.RETRIEVING), TicketStatus.RETRIEVING, Set.of(TicketStatus.READY), TicketStatus.READY, Set.of(TicketStatus.DELIVERED), TicketStatus.PARKED, Set.of(TicketStatus.CANCELLED), TicketStatus.REQUESTED, Set.of(TicketStatus.CANCELLED));
        if (!allowed.getOrDefault(from, Set.of()).contains(to))
            throw new IllegalStateException("Invalid status transition from " + from + " to " + to);
    }
}