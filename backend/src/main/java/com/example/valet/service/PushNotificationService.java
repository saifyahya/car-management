package com.example.valet.service;

import com.example.valet.entity.PushSubscriptionEntity;
import com.example.valet.entity.ValetTicket;
import com.example.valet.repository.PushSubscriptionRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.List;

@Service
public class PushNotificationService {
    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    private final PushSubscriptionRepository pushSubscriptionRepository;

    @Value("${app.vapid.public-key:}")
    private String configuredPublicKey;

    @Value("${app.vapid.private-key:}")
    private String configuredPrivateKey;

    @Value("${app.vapid.subject:mailto:valet@example.com}")
    private String subject;

    private PushService pushService;
    private String publicKeyBase64;

    public PushNotificationService(PushSubscriptionRepository pushSubscriptionRepository) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
    }

    @PostConstruct
    public void init() {
        try {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }

            if (configuredPublicKey.isBlank() || configuredPrivateKey.isBlank()) {
                log.info("Generating dynamic VAPID keypair for Web Push Notifications...");
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
                ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
                kpg.initialize(ecSpec);
                KeyPair keyPair = kpg.generateKeyPair();

                ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
                ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

                this.publicKeyBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(Utils.encode(publicKey));
                String privateKeyBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(Utils.encode(privateKey));
                this.pushService = new PushService(this.publicKeyBase64, privateKeyBase64, subject);
            } else {
                this.publicKeyBase64 = configuredPublicKey;
                this.pushService = new PushService(configuredPublicKey, configuredPrivateKey, subject);
            }
            log.info("VAPID Web Push Service initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize VAPID PushService: {}", e.getMessage(), e);
        }
    }

    public String getPublicKey() {
        return publicKeyBase64;
    }

    @Transactional
    public void saveSubscription(Long clientId, String endpoint, String p256dh, String auth) {
        pushSubscriptionRepository.findByEndpoint(endpoint).ifPresentOrElse(
                existing -> {
                    existing.setClientId(clientId);
                    existing.setP256dh(p256dh);
                    existing.setAuth(auth);
                    pushSubscriptionRepository.save(existing);
                },
                () -> {
                    PushSubscriptionEntity sub = new PushSubscriptionEntity();
                    sub.setClientId(clientId);
                    sub.setEndpoint(endpoint);
                    sub.setP256dh(p256dh);
                    sub.setAuth(auth);
                    pushSubscriptionRepository.save(sub);
                }
        );
    }

    public void sendVehicleRequestedNotification(ValetTicket ticket) {
        if (pushService == null || ticket == null || ticket.getClient() == null) {
            return;
        }

        Long clientId = ticket.getClient().getId();
        List<PushSubscriptionEntity> subscriptions = pushSubscriptionRepository.findByClientId(clientId);
        if (subscriptions.isEmpty()) {
            log.info("No active push subscriptions for client ID {}", clientId);
            return;
        }

        String payload = String.format(
                "{\"title\":\"🚗 Vehicle Requested!\",\"body\":\"%s %s %s (Plate: %s) requested at %s\",\"ticketId\":%d,\"ticketNumber\":\"%s\"}",
                ticket.getColor() != null ? ticket.getColor() : "",
                ticket.getMake() != null ? ticket.getMake() : "",
                ticket.getModel() != null ? ticket.getModel() : "",
                ticket.getPlateNumber() != null ? ticket.getPlateNumber() : "",
                ticket.getParkingLocation() != null ? ticket.getParkingLocation() : "Valet Lot",
                ticket.getId(),
                ticket.getTicketNumber()
        );

        log.info("Sending Web Push Notification to {} subscribers for ticket {}", subscriptions.size(), ticket.getTicketNumber());

        for (PushSubscriptionEntity sub : subscriptions) {
            try {
                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payload.getBytes()
                );
                pushService.send(notification);
            } catch (Exception e) {
                log.warn("Failed to send Web Push to endpoint {}: {}", sub.getEndpoint(), e.getMessage());
                if (e.getMessage() != null && (e.getMessage().contains("410") || e.getMessage().contains("404"))) {
                    pushSubscriptionRepository.findByEndpoint(sub.getEndpoint()).ifPresent(pushSubscriptionRepository::delete);
                }
            }
        }
    }
}
