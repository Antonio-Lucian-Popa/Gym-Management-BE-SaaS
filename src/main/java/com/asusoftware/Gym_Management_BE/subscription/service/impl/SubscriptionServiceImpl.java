package com.asusoftware.Gym_Management_BE.subscription.service.impl;

import com.asusoftware.Gym_Management_BE.gym.repository.GymRepository;
import com.asusoftware.Gym_Management_BE.subscription.model.Subscription;
import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import com.asusoftware.Gym_Management_BE.subscription.model.UserSubscription;
import com.asusoftware.Gym_Management_BE.subscription.model.dto.SubscriptionDto;
import com.asusoftware.Gym_Management_BE.subscription.repository.SubscriptionRepository;
import com.asusoftware.Gym_Management_BE.subscription.repository.UserSubscriptionRepository;
import com.asusoftware.Gym_Management_BE.subscription.service.SubscriptionService;

import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    /**
     * Atribuie abonamentul gratuit (Basic) unui utilizator.
     */
    @Override
    public void assignFreeSubscriptionToUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Subscription freeSubscription = findByTier(SubscriptionTier.BASIC);

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUser(user);
        userSubscription.setSubscription(freeSubscription);
        userSubscription.setStartDate(LocalDate.now());
        userSubscription.setStatus("active");

        userSubscriptionRepository.save(userSubscription);
    }

    /**
     * Verifică limita de săli bazată pe abonamentul activ al utilizatorului.
     */
    @Override
    public void validateGymLimit(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        UserSubscription activeSubscription = userSubscriptionRepository.findActiveSubscriptionByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No active subscription found for user with ID: " + userId));

        Subscription subscription = activeSubscription.getSubscription();
        long gymCount = user.getGyms().size();

        if (gymCount >= subscription.getMaxGyms()) {
            throw new RuntimeException("Gym limit exceeded for this subscription tier.");
        }
    }

    /**
     * Upgradează abonamentul unui utilizator la unul superior.
     */
    @Override
    public void upgradeUserSubscription(UUID userId, SubscriptionTier subscriptionTier) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Subscription newSubscription = findByTier(subscriptionTier);

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUser(user);
        userSubscription.setSubscription(newSubscription);
        userSubscription.setStartDate(LocalDate.now());
        userSubscription.setStatus("active");

        userSubscriptionRepository.save(userSubscription);
    }

    /**
     * Găsește un abonament pe baza tipului său (tier).
     */
    @Override
    public Subscription findByTier(SubscriptionTier tier) {
        return subscriptionRepository.findByTier(tier)
                .orElseThrow(() -> new RuntimeException("Subscription tier not found: " + tier.getTierName()));
    }

    /**
     * Obține toate abonamentele disponibile.
     */
    @Override
    public List<SubscriptionDto> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține un abonament după tipul său sub formă de DTO.
     */
    @Override
    public SubscriptionDto getSubscriptionByTier(String tier) {
        Subscription subscription = findByTier(SubscriptionTier.fromString(tier));
        return mapToDto(subscription);
    }

    /**
     * Mapează un abonament într-un DTO.
     */
    private SubscriptionDto mapToDto(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setTier(subscription.getTier());
        dto.setMaxMembers(subscription.getMaxMembers());
        dto.setMaxGyms(subscription.getMaxGyms());
        dto.setPrice(subscription.getPrice());
        return dto;
    }

}