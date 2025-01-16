package com.asusoftware.Gym_Management_BE.expense.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateExpenseDto {

    @NotNull
    private UUID gymId;

    @NotBlank
    private String category;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String description;
}