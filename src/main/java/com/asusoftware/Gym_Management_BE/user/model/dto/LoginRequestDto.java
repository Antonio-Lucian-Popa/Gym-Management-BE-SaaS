package com.asusoftware.Gym_Management_BE.user.model.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}