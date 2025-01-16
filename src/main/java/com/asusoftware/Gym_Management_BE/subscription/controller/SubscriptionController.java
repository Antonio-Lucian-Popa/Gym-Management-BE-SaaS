package com.asusoftware.Gym_Management_BE.subscription.controller;


import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import com.asusoftware.Gym_Management_BE.subscription.model.dto.SubscriptionDto;
import com.asusoftware.Gym_Management_BE.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * Obține toate abonamentele disponibile
     */
    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions() {
        List<SubscriptionDto> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    /**
     * Obține detalii despre un abonament specific
     */
    @GetMapping("/{tier}")
    public ResponseEntity<SubscriptionDto> getSubscriptionByTier(@PathVariable String tier) {
        SubscriptionDto subscription = subscriptionService.getSubscriptionByTier(tier);
        return ResponseEntity.ok(subscription);
    }

    /**
     * Atribuie abonamentul gratuit (Basic) unui utilizator
     */
    @PostMapping("/assign-free/{userId}")
    public ResponseEntity<String> assignFreeSubscription(@PathVariable UUID userId) {
        subscriptionService.assignFreeSubscriptionToUser(userId);
        return ResponseEntity.ok("Free subscription assigned successfully.");
    }

    /**
     * Upgrade la un abonament pentru o sală
     */
    @PostMapping("/upgrade/{gymId}")
    public ResponseEntity<String> upgradeSubscription(
            @PathVariable UUID gymId,
            @RequestParam SubscriptionTier tier) {
        subscriptionService.upgradeSubscription(gymId, tier);
        return ResponseEntity.ok("Subscription upgraded successfully.");
    }
}
