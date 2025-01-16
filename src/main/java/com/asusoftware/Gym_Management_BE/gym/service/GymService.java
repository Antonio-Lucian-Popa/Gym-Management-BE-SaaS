package com.asusoftware.Gym_Management_BE.gym.service;

import com.asusoftware.Gym_Management_BE.gym.model.dto.CreateGymDto;
import com.asusoftware.Gym_Management_BE.gym.model.dto.GymResponseDto;
import com.asusoftware.Gym_Management_BE.gym.model.dto.UpdateGymDto;

import java.util.List;
import java.util.UUID;

public interface GymService {

    GymResponseDto createGym(CreateGymDto createGymDto);

    GymResponseDto getGymById(UUID gymId);

    List<GymResponseDto> getGymsByOwner(UUID ownerId);

    GymResponseDto updateGym(UUID gymId, UpdateGymDto updateGymDto);

    void deleteGym(UUID gymId);
}