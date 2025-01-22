package com.asusoftware.Gym_Management_BE.subscription.model;

import com.asusoftware.Gym_Management_BE.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Utilizatorul abonat

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription; // Tipul abonamentului

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // DATE în baza de date

    @Column(name = "end_date")
    private LocalDate endDate; // DATE în baza de date

    @Column(name = "status", nullable = false)
    private String status = "active"; // Status: active, inactive, canceled

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
