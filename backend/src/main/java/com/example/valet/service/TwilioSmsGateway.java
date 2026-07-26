package com.example.valet.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@ConditionalOnProperty(name = "app.twilio.enabled", havingValue = "true")
public class TwilioSmsGateway implements SmsGateway {
    private static final Logger log = LoggerFactory.getLogger(TwilioSmsGateway.class);

    @Value("${app.twilio.account-sid:}")
    private String accountSid;

    @Value("${app.twilio.auth-token:}")
    private String authToken;

    @Value("${app.twilio.from-number:}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isBlank() && authToken != null && !authToken.isBlank()) {
            log.info("Initializing Twilio SMS Gateway with Account SID: {}", accountSid);
            Twilio.init(accountSid, authToken);
        } else {
            log.warn("Twilio SMS is enabled but Account SID or Auth Token is missing.");
        }
    }

    @Override
    public void send(String phoneNumber, String messageText) {
        try {
            log.info("Sending Twilio SMS from {} to: {}", fromNumber, phoneNumber);
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(fromNumber),
                    messageText
            ).create();
            log.info("Twilio SMS sent successfully. SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("Failed to send Twilio SMS to {}: {}", phoneNumber, e.getMessage(), e);
        }
    }
}
