package com.asusoftware.Gym_Management_BE.gym.service;

import com.asusoftware.Gym_Management_BE.gym.model.dto.*;

import java.util.List;
import java.util.UUID;

public interface GymService {

    GymResponseDto createGym(CreateGymDto createGymDto);

    GymResponseDto getGymById(UUID gymId);

    List<GymResponseDto> getGymsByOwner(UUID ownerId);

    GymResponseDto updateGym(UUID gymId, UpdateGymDto updateGymDto);

    void deleteGym(UUID gymId);


    List<GymMemberResponseDto> getMembersByGymId(UUID gymId);

    GymMemberResponseDto addMemberToGym(UUID gymId, CreateGymMemberDto createGymMemberDto);

    GymMemberResponseDto updateGymMember(UUID gymId, UUID memberId, UpdateGymMemberDto updateGymMemberDto);

    void deleteGymMember(UUID gymId, UUID memberId);

}