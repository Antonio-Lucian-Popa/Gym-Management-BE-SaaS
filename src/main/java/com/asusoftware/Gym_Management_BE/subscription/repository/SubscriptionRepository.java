package com.asusoftware.Gym_Management_BE.subscription.repository;

import com.asusoftware.Gym_Management_BE.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByTier(String tier);
}
