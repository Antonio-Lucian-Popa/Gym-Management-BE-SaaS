package com.asusoftware.Gym_Management_BE.payment.controller;

import com.asusoftware.Gym_Management_BE.payment.model.dto.PaymentRequestDto;
import com.asusoftware.Gym_Management_BE.payment.model.dto.PaymentResponseDto;
import com.asusoftware.Gym_Management_BE.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Creează o plată utilizând Stripe.
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto payment = paymentService.createPayment(paymentRequestDto);
        return ResponseEntity.ok(payment);
    }

    /**
     * Obține detaliile unei plăți după ID.
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable UUID paymentId) {
        PaymentResponseDto payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Obține toate plățile pentru un membru.
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByMember(@PathVariable UUID memberId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByMember(memberId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Webhook pentru Stripe.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        paymentService.handleStripeWebhook(payload, signature);
        return ResponseEntity.ok("Webhook handled successfully.");
    }
}