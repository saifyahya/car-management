package com.example.valet.service;

public interface SmsGateway {
    void send(String phoneNumber, String message);
}