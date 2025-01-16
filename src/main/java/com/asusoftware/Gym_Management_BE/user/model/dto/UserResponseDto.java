package com.asusoftware.Gym_Management_BE.user.model.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
}