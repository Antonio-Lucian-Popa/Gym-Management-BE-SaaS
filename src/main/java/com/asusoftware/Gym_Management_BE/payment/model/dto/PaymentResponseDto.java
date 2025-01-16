package com.asusoftware.Gym_Management_BE.payment.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentResponseDto {

    private UUID id;
    private UUID memberId;
    private UUID gymId;
    private BigDecimal amount;
    private String paymentType;
    private String status;
    private String paymentDate;
    private String receiptUrl;
}