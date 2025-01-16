package com.asusoftware.Gym_Management_BE.gym.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateGymDto {

    @NotBlank
    private String name;

    @NotNull
    private UUID ownerId; // ID-ul adminului care creează sala
}
