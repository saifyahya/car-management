package com.example.valet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "log")
public class LoggingSmsGateway implements SmsGateway {
    private static final Logger log = LoggerFactory.getLogger(LoggingSmsGateway.class);

    public void send(String phoneNumber, String message) {
        log.info("SMS to {}: {}", phoneNumber, message);
    }
}