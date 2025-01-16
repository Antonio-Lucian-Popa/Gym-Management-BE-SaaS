package com.asusoftware.Gym_Management_BE.gym.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGymDto {

    @NotBlank
    private String name;
}