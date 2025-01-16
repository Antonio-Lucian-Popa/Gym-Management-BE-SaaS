package com.asusoftware.Gym_Management_BE.subscription.service;



import com.asusoftware.Gym_Management_BE.subscription.model.Subscription;
import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import com.asusoftware.Gym_Management_BE.subscription.model.dto.SubscriptionDto;
import com.asusoftware.Gym_Management_BE.user.model.User;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    /**
     * Atribuie un abonament gratuit (Basic) unui utilizator.
     * @param userId Id-ul Utilizatorul care va primi abonamentul.
     */
    void assignFreeSubscriptionToUser(UUID userId);

    /**
     * Verifică dacă un admin poate adăuga mai mulți membri.
     * @param gymId ID-ul sălii.
     */
    void validateMemberLimit(UUID gymId);

    /**
     * Verifică dacă un admin poate crea mai multe săli.
     * @param ownerId ID-ul adminului.
     */
    void validateGymLimit(UUID ownerId);

    /**
     * Permite upgrade-ul abonamentului unui gym.
     * @param gymId ID-ul sălii.
     * @param subscriptionTier Tipul abonamentului dorit.
     */
    void upgradeSubscription(UUID gymId, SubscriptionTier subscriptionTier);

    /**
     * Găsește un abonament pe baza tipului.
     * @param tier Tipul abonamentului.
     * @return Obiectul Subscription.
     */
    Subscription findByTier(SubscriptionTier tier);

    List<SubscriptionDto> getAllSubscriptions();

    SubscriptionDto getSubscriptionByTier(String tier);
}

