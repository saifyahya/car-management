package com.example.valet.controller;

import com.example.valet.security.SecurityUtils;
import com.example.valet.service.PushNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
public class PushSubscriptionController {
    private final PushNotificationService pushNotificationService;

    public PushSubscriptionController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        return ResponseEntity.ok(Map.of("publicKey", pushNotificationService.getPublicKey()));
    }

    public record PushSubscribeRequest(String endpoint, Keys keys) {
        public record Keys(String p256dh, String auth) {}
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@RequestBody PushSubscribeRequest req) {
        Long clientId = SecurityUtils.getCurrentClientId();
        if (req == null || req.endpoint() == null || req.keys() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid subscription payload"));
        }
        pushNotificationService.saveSubscription(clientId, req.endpoint(), req.keys().p256dh(), req.keys().auth());
        return ResponseEntity.ok(Map.of("status", "subscribed"));
    }
}
