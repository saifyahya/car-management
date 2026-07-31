package com.example.valet.dto;

import com.example.valet.entity.TicketStatus;
import com.example.valet.entity.ValetTicket;

import java.time.Instant;

public record TicketResponse(Long id, String ticketNumber, String publicToken, String visitorPhone, String visitorEmail, String plateNumber,
                             String make, String model, String color, String parkingLocation, String keyLocation,
                             String assignedTo, String notes,
                             TicketStatus status, Instant checkedInAt, Instant requestedAt, Instant readyAt,
                             Instant deliveredAt, String pickupPin) {
    public static TicketResponse from(ValetTicket t) {
        return new TicketResponse(t.getId(), t.getTicketNumber(), t.getPublicToken(), t.getVisitorPhone(), t.getVisitorEmail(), t.getPlateNumber(), t.getMake(), t.getModel(), t.getColor(), t.getParkingLocation(), t.getKeyLocation(), t.getAssignedTo(), t.getNotes(), t.getStatus(), t.getCheckedInAt(), t.getRequestedAt(), t.getReadyAt(), t.getDeliveredAt(), t.getPickupPin());
    }
}