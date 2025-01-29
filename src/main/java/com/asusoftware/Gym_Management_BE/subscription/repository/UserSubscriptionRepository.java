package com.asusoftware.Gym_Management_BE.subscription.repository;

import com.asusoftware.Gym_Management_BE.subscription.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {
    List<UserSubscription> findByUserId(UUID userId);
    Optional<UserSubscription> findByUserIdAndStatus(UUID userId, String status);

    /**
     * Găsește abonamentul activ pentru un utilizator.
     * @param userId ID-ul utilizatorului.
     * @return Obiectul UserSubscription, dacă există unul activ.
     */
    @Query("SELECT us FROM UserSubscription us WHERE us.user.id = :userId AND us.status = 'ACTIVE'")
    Optional<UserSubscription> findActiveSubscriptionByUserId(UUID userId);
}