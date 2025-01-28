package com.asusoftware.Gym_Management_BE.gym.model.dto;

import java.time.LocalDate;
import java.util.UUID;

public interface GymMemberProjection {
    UUID getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhoneNumber(); // Adăugat
    String getMembershipType();
    String getMembershipStatus();
    LocalDate getStartDate();
    LocalDate getEndDate();
}

