package com.example.valet.dto;

public record DashboardResponse(long active, long parked, long requested, long retrieving, long ready, long delivered) {
}