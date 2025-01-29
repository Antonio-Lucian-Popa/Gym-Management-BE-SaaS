package com.asusoftware.Gym_Management_BE.gym.service.impl;

import com.asusoftware.Gym_Management_BE.gym.model.Gym;
import com.asusoftware.Gym_Management_BE.gym.model.GymMember;
import com.asusoftware.Gym_Management_BE.gym.model.dto.*;
import com.asusoftware.Gym_Management_BE.gym.repository.GymMemberRepository;
import com.asusoftware.Gym_Management_BE.gym.repository.GymRepository;
import com.asusoftware.Gym_Management_BE.gym.service.GymService;
import com.asusoftware.Gym_Management_BE.subscription.service.SubscriptionService;
import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return gymRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResponse<GymMemberProjection> getMembersByGymId(UUID gymId, String filter, Pageable pageable) {
        gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found with ID: " + gymId));

        // 🛠 Aplică filtrarea și extrage valorile corect
        String firstNameFilter = extractFilterValue(filter, "firstName");
        String lastNameFilter = extractFilterValue(filter, "lastName");
        String emailFilter = extractFilterValue(filter, "email");

        Page<GymMemberProjection> page = gymMemberRepository.findMembersByGymIdAndFilter(
                gymId, firstNameFilter, lastNameFilter, emailFilter, pageable
        );

        return new PagedResponse<>(page);
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

    @Override
    public GymMemberResponseDto addMemberToGym(UUID gymId, CreateGymMemberDto createGymMemberDto) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found with ID: " + gymId));

        User user = userRepository.findByEmail(createGymMemberDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + createGymMemberDto.getEmail()));

        GymMember gymMember = new GymMember();
        gymMember.setUser(user);
        gymMember.setGym(gym);
        gymMember.setMembershipType(createGymMemberDto.getMembershipType());
        gymMember.setStartDate(createGymMemberDto.getStartDate() != null ? createGymMemberDto.getStartDate() : LocalDate.now());
        gymMember.setEndDate(createGymMemberDto.getEndDate());

        gymMember = gymMemberRepository.save(gymMember);
        return mapToGymMemberResponseDto(gymMember);
    }

    @Override
    public GymMemberResponseDto updateGymMember(UUID gymId, UUID memberId, UpdateGymMemberDto updateGymMemberDto) {
        GymMember gymMember = gymMemberRepository.findByIdAndGymId(memberId, gymId)
                .orElseThrow(() -> new RuntimeException("Gym member not found"));

        gymMember.setMembershipType(updateGymMemberDto.getMembershipType());
        gymMember.setStartDate(updateGymMemberDto.getStartDate());
        gymMember.setEndDate(updateGymMemberDto.getEndDate());
        gymMember.setMembershipStatus(updateGymMemberDto.getMembershipStatus());

        gymMember = gymMemberRepository.save(gymMember);
        return mapToGymMemberResponseDto(gymMember);
    }

    @Override
    public void deleteGymMember(UUID gymId, UUID memberId) {
        GymMember gymMember = gymMemberRepository.findByIdAndGymId(memberId, gymId)
                .orElseThrow(() -> new RuntimeException("Gym member not found"));

        gymMemberRepository.delete(gymMember);
    }

    /**
     * 🛠 Metodă pentru extragerea valorii unui filtru de tip "firstName:ge"
     */
    private String extractFilterValue(String filter, String key) {
        if (filter == null || filter.isBlank()) {
            return "";
        }

        String[] filters = filter.split(",");
        for (String f : filters) {
            String[] keyValue = f.split(":");
            if (keyValue.length == 2 && keyValue[0].equalsIgnoreCase(key)) {
                return "%" + keyValue[1] + "%"; // Adăugăm `%` pentru LIKE
            }
        }
        return "";
    }

    private GymResponseDto mapToResponseDto(Gym gym) {
        return new GymResponseDto(gym.getId(), gym.getName(), gym.getOwner().getId());
    }

    private GymMemberResponseDto mapToGymMemberResponseDto(GymMember member) {
        return new GymMemberResponseDto(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getFirstName(),
                member.getUser().getLastName(),
                member.getUser().getEmail(),
                member.getUser().getPhone(),
                member.getMembershipType(),
                member.getMembershipStatus(),
                member.getStartDate(),
                member.getEndDate()
        );
    }
}
