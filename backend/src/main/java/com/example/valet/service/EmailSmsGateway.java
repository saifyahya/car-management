package com.example.valet.service;

import com.example.valet.entity.ValetTicket;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Primary
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "mail", matchIfMissing = true)
public class EmailSmsGateway implements SmsGateway {
    private static final Logger log = LoggerFactory.getLogger(EmailSmsGateway.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")
            .withZone(ZoneId.systemDefault());

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final EmailTemplateService templateService;

    @Value("${app.mail.from:noreply@hotelvalet.com}")
    private String fromEmail;

    public EmailSmsGateway(ObjectProvider<JavaMailSender> mailSenderProvider, EmailTemplateService templateService) {
        this.mailSenderProvider = mailSenderProvider;
        this.templateService = templateService;
    }

    @Override
    public void send(String recipient, String messageText) {
        log.info("Sending Generic Email Notification to [{}]: {}", recipient, messageText);
        
        Map<String, Object> model = new HashMap<>();
        model.put("messageText", messageText);
        model.put("clientName", "Hotel Valet");

        String htmlContent = templateService.processTemplate("generic-notification.ftlh", model);
        if (htmlContent == null) {
            htmlContent = "<p>" + messageText + "</p>";
        }

        sendHtmlEmail(recipient, "Hotel Valet Notification", htmlContent, messageText);
    }

    public void sendTicketCheckInEmail(ValetTicket ticket, String requestUrl) {
        if (ticket.getVisitorEmail() == null || ticket.getVisitorEmail().isBlank()) {
            log.info("Skipping ticket check-in email for ticket {}: no email provided", ticket.getTicketNumber());
            return;
        }

        String recipient = ticket.getVisitorEmail();
        String clientName = (ticket.getClient() != null && ticket.getClient().getName() != null) 
                ? ticket.getClient().getName() : "Hotel Valet";

        String vehicleInfo = buildVehicleInfo(ticket);

        Map<String, Object> model = new HashMap<>();
        model.put("clientName", clientName);
        model.put("ticketNumber", ticket.getTicketNumber());
        model.put("pickupPin", ticket.getPickupPin());
        model.put("vehicleInfo", vehicleInfo);
        model.put("plateNumber", ticket.getPlateNumber());
        model.put("checkedInTime", ticket.getCheckedInAt() != null ? DATE_FORMATTER.format(ticket.getCheckedInAt()) : "");
        model.put("requestUrl", requestUrl);

        String htmlContent = templateService.processTemplate("ticket-checkin.ftlh", model);
        String plainText = "Welcome to " + clientName + ". Your valet ticket is " + ticket.getTicketNumber() + 
                ". Pickup PIN: " + ticket.getPickupPin() + ". Request your vehicle: " + requestUrl;

        String subject = "Valet Ticket #" + ticket.getTicketNumber() + " - Welcome to " + clientName;

        sendHtmlEmail(recipient, subject, htmlContent != null ? htmlContent : plainText, plainText);
    }

    public void sendVehicleReadyEmail(ValetTicket ticket) {
        if (ticket.getVisitorEmail() == null || ticket.getVisitorEmail().isBlank()) {
            log.info("Skipping vehicle ready email for ticket {}: no email provided", ticket.getTicketNumber());
            return;
        }

        String recipient = ticket.getVisitorEmail();
        String clientName = (ticket.getClient() != null && ticket.getClient().getName() != null) 
                ? ticket.getClient().getName() : "Hotel Valet";

        String vehicleInfo = buildVehicleInfo(ticket);

        Map<String, Object> model = new HashMap<>();
        model.put("clientName", clientName);
        model.put("ticketNumber", ticket.getTicketNumber());
        model.put("pickupPin", ticket.getPickupPin());
        model.put("vehicleInfo", vehicleInfo);
        model.put("plateNumber", ticket.getPlateNumber());

        String htmlContent = templateService.processTemplate("vehicle-ready.ftlh", model);
        String plainText = "Your vehicle is ready for pickup! Ticket " + ticket.getTicketNumber() + 
                ". Pickup PIN: " + ticket.getPickupPin();

        String subject = "Vehicle Ready! Ticket #" + ticket.getTicketNumber() + " - " + clientName;

        sendHtmlEmail(recipient, subject, htmlContent != null ? htmlContent : plainText, plainText);
    }

    private void sendHtmlEmail(String recipient, String subject, String htmlContent, String plainTextFallback) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender is not configured. Email skipped for recipient [{}], subject [{}]", recipient, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(plainTextFallback, htmlContent);

            mailSender.send(message);
            log.info("HTML Email notification successfully sent to [{}] for subject [{}]", recipient, subject);
        } catch (Exception e) {
            log.error("Failed to send HTML Email notification to [{}]: {}", recipient, e.getMessage(), e);
        }
    }

    private String buildVehicleInfo(ValetTicket ticket) {
        StringBuilder sb = new StringBuilder();
        if (ticket.getColor() != null && !ticket.getColor().isBlank()) {
            sb.append(ticket.getColor()).append(" ");
        }
        if (ticket.getMake() != null && !ticket.getMake().isBlank()) {
            sb.append(ticket.getMake()).append(" ");
        }
        if (ticket.getModel() != null && !ticket.getModel().isBlank()) {
            sb.append(ticket.getModel());
        }
        return sb.toString().trim();
    }
}
