package com.asusoftware.Gym_Management_BE.user.service;


import com.asusoftware.Gym_Management_BE.user.model.dto.CreateUserDto;
import com.asusoftware.Gym_Management_BE.user.model.dto.LoginRequestDto;
import com.asusoftware.Gym_Management_BE.user.model.dto.UpdateUserDto;
import com.asusoftware.Gym_Management_BE.user.model.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(CreateUserDto createUserDto);
    UserResponseDto getUserByKeycloakId(UUID keycloakId);
    UserResponseDto getUserById(UUID id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(UUID id, UpdateUserDto updateUserDto);
    void deleteUser(UUID id);
}
