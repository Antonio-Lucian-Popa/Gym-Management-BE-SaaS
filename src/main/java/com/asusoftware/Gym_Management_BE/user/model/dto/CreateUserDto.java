package com.asusoftware.Gym_Management_BE.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    private String phone;

    @NotNull
    private String role; // ADMIN sau USER

    @NotBlank
    private String password; // Parola pentru Keycloak
}
