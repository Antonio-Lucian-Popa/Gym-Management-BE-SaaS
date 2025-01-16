package com.asusoftware.Gym_Management_BE.expense.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateExpenseDto {

    @NotBlank
    private String category;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String description;
}