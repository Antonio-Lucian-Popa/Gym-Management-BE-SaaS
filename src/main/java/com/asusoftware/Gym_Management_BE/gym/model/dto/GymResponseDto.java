package com.asusoftware.Gym_Management_BE.gym.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GymResponseDto {

    private UUID id;
    private String name;
    private UUID ownerId;
}
