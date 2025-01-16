package com.asusoftware.Gym_Management_BE.gym.model;

import com.asusoftware.Gym_Management_BE.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "gyms")
public class Gym {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // Legătură cu utilizatorul admin

    @Column(name = "stripe_customer_id", unique = true)
    private String stripeCustomerId;

    @Column(name = "subscription_status", nullable = false)
    private String subscriptionStatus = "inactive";

    @Column(name = "subscription_tier")
    private String subscriptionTier; // Basic, Pro, Enterprise

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
