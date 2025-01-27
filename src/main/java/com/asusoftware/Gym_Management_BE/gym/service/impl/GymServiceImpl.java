package com.asusoftware.Gym_Management_BE.gym.service.impl;

import com.asusoftware.Gym_Management_BE.gym.model.Gym;
import com.asusoftware.Gym_Management_BE.gym.model.GymMember;
import com.asusoftware.Gym_Management_BE.gym.model.dto.CreateGymDto;
import com.asusoftware.Gym_Management_BE.gym.model.dto.GymMemberResponseDto;
import com.asusoftware.Gym_Management_BE.gym.model.dto.GymResponseDto;
import com.asusoftware.Gym_Management_BE.gym.model.dto.UpdateGymDto;
import com.asusoftware.Gym_Management_BE.gym.repository.GymMemberRepository;
import com.asusoftware.Gym_Management_BE.gym.repository.GymRepository;
import com.asusoftware.Gym_Management_BE.gym.service.GymService;
import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import com.asusoftware.Gym_Management_BE.subscription.service.SubscriptionService;
import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GymServiceImpl implements GymService {

    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final GymMemberRepository gymMemberRepository;

    public GymServiceImpl(GymRepository gymRepository, UserRepository userRepository, SubscriptionService subscriptionService, GymMemberRepository gymMemberRepository) {
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
        this.gymMemberRepository = gymMemberRepository;
    }

    @Override
    public GymResponseDto createGym(CreateGymDto createGymDto) {
        User owner = userRepository.findById(createGymDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        // Validează limitele abonamentului înainte de a crea sala
        subscriptionService.validateGymLimit(owner.getId());

        Gym gym = new Gym();
        gym.setName(createGymDto.getName());
        gym.setOwner(owner);

        gym = gymRepository.save(gym);
        return mapToResponseDto(gym);
    }

    @Override
    public GymResponseDto getGymById(UUID gymId) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found"));
        return mapToResponseDto(gym);
    }

    @Override
    public List<GymResponseDto> getGymsByOwner(UUID ownerId) {
        List<Gym> gyms = gymRepository.findByOwnerId(ownerId);
        return gyms.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<GymMemberResponseDto> getMembersByGymId(UUID gymId) {
        // Verifică dacă sala există
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found with ID: " + gymId));

        // Obține membrii sălii
        List<GymMember> members = gymMemberRepository.findByGymId(gymId);

        // Mapează membrii la DTO-uri pentru răspuns
        return members.stream()
                .map(this::mapToGymMemberResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public GymResponseDto updateGym(UUID gymId, UpdateGymDto updateGymDto) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found"));
        gym.setName(updateGymDto.getName());
        gym = gymRepository.save(gym);
        return mapToResponseDto(gym);
    }

    @Override
    public void deleteGym(UUID gymId) {
        gymRepository.deleteById(gymId);
    }

    private GymResponseDto mapToResponseDto(Gym gym) {
        GymResponseDto dto = new GymResponseDto();
        dto.setId(gym.getId());
        dto.setName(gym.getName());
        dto.setOwnerId(gym.getOwner().getId());
        return dto;
    }

    private GymMemberResponseDto mapToGymMemberResponseDto(GymMember member) {
        GymMemberResponseDto dto = new GymMemberResponseDto();
        dto.setId(member.getId());
        dto.setUserId(member.getUser().getId());
        dto.setFirstName(member.getUser().getFirstName());
        dto.setLastName(member.getUser().getLastName());
        dto.setMembershipType(member.getMembershipType());
        dto.setMembershipStatus(member.getMembershipStatus());
        dto.setStartDate(member.getStartDate());
        dto.setEndDate(member.getEndDate());
        return dto;
    }
}