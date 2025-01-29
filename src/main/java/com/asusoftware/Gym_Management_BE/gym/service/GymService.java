package com.asusoftware.Gym_Management_BE.gym.service;

import com.asusoftware.Gym_Management_BE.gym.model.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GymService {

    GymResponseDto createGym(CreateGymDto createGymDto);

    GymResponseDto getGymById(UUID gymId);

    List<GymResponseDto> getGymsByOwner(UUID ownerId);

    GymResponseDto updateGym(UUID gymId, UpdateGymDto updateGymDto);

    void deleteGym(UUID gymId);

    PagedResponse<GymMemberProjection> getMembersByGymId(UUID gymId, String filter, Pageable pageable);

    GymMemberResponseDto addMemberToGym(UUID gymId, CreateGymMemberDto createGymMemberDto);

    GymMemberResponseDto updateGymMember(UUID gymId, UUID memberId, UpdateGymMemberDto updateGymMemberDto);

    void deleteGymMember(UUID gymId, UUID memberId);
}
