package com.asusoftware.Gym_Management_BE.gym.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateGymMemberDto {
    @NotNull
    @Email
    private String email; // Email-ul utilizatorului care devine membru

    @NotNull
    private String membershipType;

    private LocalDate startDate;

    private LocalDate endDate;
}