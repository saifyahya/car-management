package com.example.valet.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "valet_tickets", indexes = {@Index(name = "idx_ticket_public_token", columnList = "publicToken", unique = true), @Index(name = "idx_ticket_status", columnList = "status")})
public class ValetTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @Column(nullable = false, unique = true, length = 30)
    private String ticketNumber;
    @Column(nullable = false, unique = true, length = 80)
    private String publicToken;
    @Column(nullable = false, length = 30)
    private String visitorPhone;
    @Column(nullable = false, length = 30)
    private String plateNumber;
    @Column(nullable = false, length = 50)
    private String make;
    @Column(nullable = false, length = 50)
    private String model;
    @Column(nullable = false, length = 30)
    private String color;
    @Column(length = 50)
    private String parkingLocation;
    @Column(length = 50)
    private String keyLocation;
    @Column(length = 100)
    private String assignedTo;
    @Column(length = 500)
    private String notes;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status;
    @Column(nullable = false)
    private Instant checkedInAt;
    private Instant requestedAt;
    private Instant readyAt;
    private Instant deliveredAt;
    @Column(nullable = false, length = 6)
    private String pickupPin;

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String v) {
        ticketNumber = v;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String v) {
        publicToken = v;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setVisitorPhone(String v) {
        visitorPhone = v;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String v) {
        plateNumber = v;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String v) {
        make = v;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String v) {
        model = v;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String v) {
        color = v;
    }

    public String getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(String v) {
        parkingLocation = v;
    }

    public String getKeyLocation() {
        return keyLocation;
    }

    public void setKeyLocation(String v) {
        keyLocation = v;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String v) {
        assignedTo = v;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String v) {
        notes = v;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus v) {
        status = v;
    }

    public Instant getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(Instant v) {
        checkedInAt = v;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant v) {
        requestedAt = v;
    }

    public Instant getReadyAt() {
        return readyAt;
    }

    public void setReadyAt(Instant v) {
        readyAt = v;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant v) {
        deliveredAt = v;
    }

    public String getPickupPin() {
        return pickupPin;
    }

    public void setPickupPin(String v) {
        pickupPin = v;
    }
}