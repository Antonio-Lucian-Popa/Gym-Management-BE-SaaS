package com.asusoftware.Gym_Management_BE.payment.service;

import com.asusoftware.Gym_Management_BE.payment.model.dto.PaymentRequestDto;
import com.asusoftware.Gym_Management_BE.payment.model.dto.PaymentResponseDto;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);

    PaymentResponseDto getPaymentById(UUID paymentId);

    List<PaymentResponseDto> getPaymentsByMember(UUID memberId);

    void handleStripeWebhook(String payload, String signature);
}
