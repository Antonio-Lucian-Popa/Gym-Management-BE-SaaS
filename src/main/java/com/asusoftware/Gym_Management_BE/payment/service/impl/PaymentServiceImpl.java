package com.asusoftware.Gym_Management_BE.payment.service.impl;

import com.asusoftware.Gym_Management_BE.gym.model.Gym;
import com.asusoftware.Gym_Management_BE.gym.model.GymMember;
import com.asusoftware.Gym_Management_BE.gym.repository.GymMemberRepository;
import com.asusoftware.Gym_Management_BE.gym.repository.GymRepository;
import com.asusoftware.Gym_Management_BE.payment.PaymentRepository;
import com.asusoftware.Gym_Management_BE.payment.model.MemberPayment;
import com.asusoftware.Gym_Management_BE.payment.model.dto.PaymentRequestDto;
import com.asusoftware.Gym_Management_BE.payment.model.dto.PaymentResponseDto;
import com.asusoftware.Gym_Management_BE.payment.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import java.time.LocalDate;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private GymRepository gymRepository;
    @Autowired
    private GymMemberRepository gymMemberRepository;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;


    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        // Găsește sala
        Gym gym = gymRepository.findById(paymentRequestDto.getGymId())
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        // Găsește membrul
        GymMember member = gymMemberRepository.findById(paymentRequestDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Gym member not found"));

        try {
            // Creează plata Stripe
            PaymentIntent paymentIntent = createStripePayment(paymentRequestDto);

            // Salvează plata în baza de date
            MemberPayment payment = savePayment(paymentRequestDto, gym, member, paymentIntent);

            // Returnează răspunsul
            return mapToResponseDto(payment);
        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment creation failed: " + e.getMessage());
        }
    }



    @Override
    public PaymentResponseDto getPaymentById(UUID paymentId) {
        MemberPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToResponseDto(payment);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByMember(UUID memberId) {
        return paymentRepository.findByMemberId(memberId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void handleStripeWebhook(String payload, String signature) {
        try {
            Webhook.constructEvent(payload, signature, webhookSecret);
            // Aici poți să gestionezi actualizarea plăților pe baza evenimentelor Stripe
            System.out.println("Webhook handled successfully.");
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Webhook signature verification failed: " + e.getMessage());
        }
    }

    private PaymentIntent createStripePayment(PaymentRequestDto dto) throws StripeException {
        return PaymentIntent.create(PaymentIntentCreateParams.builder()
                .setAmount(dto.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // Stripe acceptă suma în cenți
                .setCurrency("usd")
                .setReceiptEmail(dto.getReceiptEmail()) // Email pentru chitanță
                .build());
    }

    private MemberPayment savePayment(PaymentRequestDto dto, Gym gym, GymMember member, PaymentIntent paymentIntent) {
        MemberPayment payment = new MemberPayment();
        payment.setGym(gym);
        payment.setMember(member); // Asociază membrul corect
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(LocalDate.now());
        payment.setDescription(dto.getDescription());
        payment.setPaymentType(dto.getPaymentType());
        payment.setStatus("pending");
        payment.setStripePaymentId(paymentIntent.getId()); // ID Stripe
        payment.setStripeCustomerId(paymentIntent.getCustomer()); // ID client Stripe
        payment.setReceiptUrl(paymentIntent.getCharges().getData().get(0).getReceiptUrl()); // URL chitanță

        return paymentRepository.save(payment);
    }

    private PaymentResponseDto mapToResponseDto(MemberPayment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setMemberId(payment.getMember().getId());
        dto.setGymId(payment.getGym().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate().toString());
        dto.setDescription(payment.getDescription());
        dto.setReceiptUrl(payment.getReceiptUrl());
        return dto;
    }
}

