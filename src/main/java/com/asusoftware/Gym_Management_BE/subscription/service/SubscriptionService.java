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
     *
     * @param userId ID-ul utilizatorului care va primi abonamentul.
     */
    void assignFreeSubscriptionToUser(UUID userId);

    /**
     * Verifică dacă un utilizator poate crea mai multe săli pe baza abonamentului.
     *
     * @param userId ID-ul utilizatorului care încearcă să creeze o sală.
     */
    void validateGymLimit(UUID userId);

    /**
     * Permite upgrade-ul abonamentului unui utilizator.
     *
     * @param userId ID-ul utilizatorului.
     * @param subscriptionTier Tipul abonamentului dorit.
     */
  //  void upgradeSubscription(UUID userId, SubscriptionTier subscriptionTier);

    /**
     * Găsește un abonament pe baza tipului.
     *
     * @param tier Tipul abonamentului.
     * @return Obiectul Subscription.
     */
    Subscription findByTier(SubscriptionTier tier);

    /**
     * Obține toate abonamentele disponibile.
     *
     * @return O listă de SubscriptionDto cu toate abonamentele.
     */
    List<SubscriptionDto> getAllSubscriptions();

    /**
     * Obține un abonament pe baza numelui tipului.
     *
     * @param tier Tipul abonamentului ca String.
     * @return SubscriptionDto asociat tipului.
     */
    SubscriptionDto getSubscriptionByTier(String tier);

    void upgradeUserSubscription(UUID userId, SubscriptionTier subscriptionTier);
}

