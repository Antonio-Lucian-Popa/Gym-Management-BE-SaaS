package com.asusoftware.Gym_Management_BE.payment.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDto {

    @NotNull
    private UUID memberId;

    @NotNull
    private UUID gymId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String paymentType; // e.g., "Card"

    @NotNull
    private String receiptEmail; // Email pentru chitanță
}
