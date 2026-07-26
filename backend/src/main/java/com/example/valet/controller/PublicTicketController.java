package com.example.valet.controller;

import com.example.valet.dto.TicketResponse;
import com.example.valet.service.ValetTicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/tickets")
public class PublicTicketController {
    private final ValetTicketService service;

    public PublicTicketController(ValetTicketService service) {
        this.service = service;
    }

    @GetMapping("/{token}")
    public TicketResponse get(@PathVariable String token) {
        return service.getPublic(token);
    }

    @PostMapping("/{token}/request")
    public TicketResponse request(@PathVariable String token) {
        return service.request(token);
    }
}