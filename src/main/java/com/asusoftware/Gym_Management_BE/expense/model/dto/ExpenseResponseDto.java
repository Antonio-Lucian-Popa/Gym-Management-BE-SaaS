package com.asusoftware.Gym_Management_BE.expense.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ExpenseResponseDto {

    private UUID id;
    private UUID gymId;
    private String category;
    private BigDecimal amount;
    private String description;
    private String date;
}
