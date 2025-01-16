package com.asusoftware.Gym_Management_BE.user.controller;


import com.asusoftware.Gym_Management_BE.user.model.dto.CreateUserDto;
import com.asusoftware.Gym_Management_BE.user.model.dto.UpdateUserDto;
import com.asusoftware.Gym_Management_BE.user.model.dto.UserResponseDto;
import com.asusoftware.Gym_Management_BE.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint pentru crearea unui utilizator (Admin sau User)
     */
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserResponseDto createdUser = userService.createUser(createUserDto);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Endpoint pentru obținerea detaliilor unui utilizator după ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint pentru obținerea listei de utilizatori
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint pentru actualizarea unui utilizator
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        UserResponseDto updatedUser = userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint pentru ștergerea unui utilizator
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
