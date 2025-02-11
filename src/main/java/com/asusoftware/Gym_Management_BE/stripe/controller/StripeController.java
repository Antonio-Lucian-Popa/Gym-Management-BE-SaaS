package com.asusoftware.Gym_Management_BE.stripe.controller;

import com.asusoftware.Gym_Management_BE.stripe.service.StripeService;
import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class StripeController {

    @Autowired
    private StripeService stripeService;
    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint pentru a crea un client Stripe pentru utilizator.
     * @param userId ID-ul utilizatorului din baza de date
     * @return ID-ul clientului Stripe creat
     * @throws StripeException Dacă apare o eroare Stripe
     */
    @PostMapping("/create-customer/{userId}")
    public ResponseEntity<String> createCustomer(@PathVariable UUID userId) throws StripeException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        String customerId = stripeService.createCustomer(userOpt.get());
        return ResponseEntity.ok(customerId);
    }

    /**
     * Creează un abonament Stripe pentru utilizator.
     * @param userId ID-ul utilizatorului
     * @param priceId ID-ul prețului Stripe al abonamentului
     * @return ID-ul abonamentului creat
     * @throws StripeException Dacă apare o eroare Stripe
     */
    @PostMapping("/create-subscription/{userId}")
    public ResponseEntity<String> createSubscription(@PathVariable UUID userId, @RequestParam String priceId) throws StripeException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStripeCustomerId() == null) {
            return ResponseEntity.badRequest().body("User not found or missing Stripe customer ID");
        }
        Subscription subscription = stripeService.createSubscription(userOpt.get().getStripeCustomerId(), priceId);
        return ResponseEntity.ok(subscription.getId());
    }

    /**
     * Endpoint pentru gestionarea webhook-urilor Stripe.
     * @param payload Datele webhook-ului
     * @param sigHeader Semnătura Stripe
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        stripeService.processWebhook(payload, sigHeader);
        return ResponseEntity.ok("Webhook received successfully");
    }
}