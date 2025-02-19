package com.asusoftware.Gym_Management_BE.gym.service.impl;

import com.asusoftware.Gym_Management_BE.gym.model.Gym;
import com.asusoftware.Gym_Management_BE.gym.model.GymMember;
import com.asusoftware.Gym_Management_BE.gym.model.dto.*;
import com.asusoftware.Gym_Management_BE.gym.repository.GymMemberRepository;
import com.asusoftware.Gym_Management_BE.gym.repository.GymRepository;
import com.asusoftware.Gym_Management_BE.gym.service.GymService;
import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import com.asusoftware.Gym_Management_BE.subscription.service.SubscriptionService;
import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public GymMemberResponseDto addMemberToGym(UUID gymId, CreateGymMemberDto createGymMemberDto) {
        // Găsește sala
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found with ID: " + gymId));

        // Găsește utilizatorul după email
        User user = userRepository.findByEmail(createGymMemberDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + createGymMemberDto.getEmail()));

        // Creează membrul sălii
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
    public Page<GymMemberResponseDto> getMembersByGymIdWithFilterAndSort(UUID gymId, int page, int size, String sort, String filter) {
        // 1. Verifică dacă sala există
        gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found with ID: " + gymId));

        // 2. Construiește criteriile de sortare
        Sort sorting = Sort.unsorted();
        if (!sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        }

        // 3. Aplicați paginația
        Pageable pageable = PageRequest.of(page, size, sorting);

        // 4. Aplică filtrarea
        Specification<GymMember> specification = (root, query, criteriaBuilder) -> {
            if (!filter.isEmpty()) {
                String[] filterParams = filter.split(":");
                return criteriaBuilder.like(root.get(filterParams[0]), "%" + filterParams[1] + "%");
            }
            return null;
        };

        // 5. Obține membrii filtrați, sortați și paginați
        Page<GymMember> membersPage = gymMemberRepository.findAll(Specification.where(specification), pageable);

        // 6. Mapează rezultatele în DTO-uri
        return membersPage.map(this::mapToGymMemberResponseDto);
    }

    @Override
    public Page<GymMemberProjection> getMembersByGymId(UUID gymId, String filter, Pageable pageable) {
        String firstNameFilter = extractFilterValue(filter, "firstName");
        String lastNameFilter = extractFilterValue(filter, "lastName");
        String emailFilter = extractFilterValue(filter, "email");

        return gymMemberRepository.findMembersByGymIdAndFilter(gymId, firstNameFilter, lastNameFilter, emailFilter, pageable);
    }


    @Override
    public GymMemberResponseDto updateGymMember(UUID gymId, UUID memberId, UpdateGymMemberDto updateGymMemberDto) {
        // Găsește membrul sălii
        GymMember gymMember = gymMemberRepository.findByIdAndGymId(memberId, gymId)
                .orElseThrow(() -> new RuntimeException("Gym member not found"));

        // Actualizează informațiile membrului
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

    private String extractFilterValue(String filter, String field) {
        String[] filters = filter.split(",");
        for (String f : filters) {
            if (f.startsWith(field + ":")) {
                return f.split(":")[1]; // Extrage valoarea după ":"
            }
        }
        return "";
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