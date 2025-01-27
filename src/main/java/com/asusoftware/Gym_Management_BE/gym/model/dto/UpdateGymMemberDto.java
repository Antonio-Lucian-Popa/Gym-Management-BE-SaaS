package com.asusoftware.Gym_Management_BE.gym.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateGymMemberDto {
    @NotNull
    private String membershipType;

    private LocalDate startDate;

    private LocalDate endDate;

    private String membershipStatus;
}