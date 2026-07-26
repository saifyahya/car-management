package com.example.valet.controller;

import com.example.valet.dto.AssignTicketRequest;
import com.example.valet.dto.CreateTicketRequest;
import com.example.valet.dto.DashboardResponse;
import com.example.valet.dto.TicketResponse;
import com.example.valet.entity.TicketStatus;
import com.example.valet.service.ValetTicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class ValetController {
    private final ValetTicketService service;

    public ValetController(ValetTicketService service) {
        this.service = service;
    }

    @PostMapping
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest r) {
        return service.create(r);
    }

    @GetMapping
    public List<TicketResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/{id}/assign")
    public TicketResponse assign(@PathVariable Long id, @Valid @RequestBody AssignTicketRequest r) {
        return service.assign(id, r);
    }

    @PostMapping("/{id}/status/{status}")
    public TicketResponse status(@PathVariable Long id, @PathVariable TicketStatus status) {
        return service.transition(id, status);
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }
}