package com.asusoftware.Gym_Management_BE.gym.model.dto;

import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import lombok.Data;

import java.util.UUID;

@Data
public class GymResponseDto {

    private UUID id;
    private String name;
    private UUID ownerId;
}
