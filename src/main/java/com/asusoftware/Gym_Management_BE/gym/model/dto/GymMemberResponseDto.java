package com.asusoftware.Gym_Management_BE.gym.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GymMemberResponseDto {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String membershipType;
    private String membershipStatus;
    private LocalDate startDate;
    private LocalDate endDate;
}
