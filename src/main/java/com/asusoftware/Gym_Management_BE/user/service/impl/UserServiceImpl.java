package com.asusoftware.Gym_Management_BE.user.service.impl;


import com.asusoftware.Gym_Management_BE.config.KeycloakService;
import com.asusoftware.Gym_Management_BE.subscription.service.SubscriptionService;
import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.model.dto.CreateUserDto;
import com.asusoftware.Gym_Management_BE.user.model.dto.UpdateUserDto;
import com.asusoftware.Gym_Management_BE.user.model.dto.UserResponseDto;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import com.asusoftware.Gym_Management_BE.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final KeycloakService keycloakService;

    public UserServiceImpl(UserRepository userRepository, SubscriptionService subscriptionService, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
        this.keycloakService = keycloakService;
    }

    @Override
    public UserResponseDto createUser(CreateUserDto createUserDto) {
        // 1. Creează utilizatorul în Keycloak
        String keycloakId = keycloakService.createKeycloakUser(createUserDto);

        // 2. Salvează utilizatorul în baza de date
        User user = new User();
        user.setKeycloakId(UUID.fromString(keycloakId));
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setPhone(createUserDto.getPhone());
        user.setRole(createUserDto.getRole());
        user = userRepository.save(user);

        // 3. Atribuie automat abonamentul FREE (doar pentru ADMIN)
        if ("GYM_ADMIN".equalsIgnoreCase(user.getRole())) {
            subscriptionService.assignFreeSubscriptionToUser(user.getId());
        }

        return mapToResponseDto(user);
    }

    @Override
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUser(UUID id, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
        user.setPhone(updateUserDto.getPhone());
        user = userRepository.save(user);
        return mapToResponseDto(user);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        keycloakService.deleteKeycloakUser(user.getKeycloakId().toString());
        userRepository.deleteById(id);
    }

    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId().toString());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }
}