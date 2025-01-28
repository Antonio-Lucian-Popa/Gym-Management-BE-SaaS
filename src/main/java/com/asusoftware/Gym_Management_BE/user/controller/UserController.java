package com.asusoftware.Gym_Management_BE.user.controller;


import com.asusoftware.Gym_Management_BE.config.KeycloakService;
import com.asusoftware.Gym_Management_BE.user.model.dto.*;
import com.asusoftware.Gym_Management_BE.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final KeycloakService keycloakService;

    public UserController(UserService userService, KeycloakService keycloakService) {
        this.userService = userService;
        this.keycloakService = keycloakService;
    }

    /**
     * Endpoint pentru crearea unui utilizator (Admin sau User)
     */
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserResponseDto createdUser = userService.createUser(createUserDto);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        String accessToken = keycloakService.loginUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setAccessToken(accessToken);
        return ResponseEntity.ok(loginResponse);
    }

    // Endpoint pentru obținerea utilizatorului după keycloakId
    @GetMapping("/by-keycloak-id/{keycloakId}")
    public ResponseEntity<UserResponseDto> getUserByKeycloakId(@PathVariable UUID keycloakId) {
        UserResponseDto user = userService.getUserByKeycloakId(keycloakId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable @Email String email) {
        UserResponseDto user = userService.getUserByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8));
        return ResponseEntity.ok(user);
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
