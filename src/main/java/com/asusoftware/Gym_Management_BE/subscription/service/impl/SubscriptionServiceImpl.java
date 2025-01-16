package com.asusoftware.Gym_Management_BE.subscription.service.impl;

import com.asusoftware.Gym_Management_BE.gym.model.Gym;
import com.asusoftware.Gym_Management_BE.gym.repository.GymRepository;
import com.asusoftware.Gym_Management_BE.subscription.model.Subscription;
import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import com.asusoftware.Gym_Management_BE.subscription.model.dto.SubscriptionDto;
import com.asusoftware.Gym_Management_BE.subscription.repository.SubscriptionRepository;
import com.asusoftware.Gym_Management_BE.subscription.service.SubscriptionService;

import com.asusoftware.Gym_Management_BE.user.model.User;
import com.asusoftware.Gym_Management_BE.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private GymRepository gymRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void assignFreeSubscriptionToUser(UUID userId) {
        // Găsește utilizatorul după ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Obține abonamentul FREE (Basic)
        Subscription freeSubscription = findByTier(SubscriptionTier.BASIC);

        Gym gym = new Gym();
        gym.setOwner(user);
        gym.setSubscriptionTier(freeSubscription.getTier());
        gym.setSubscriptionStatus("active");

        gymRepository.save(gym);
    }


    @Override
    public void validateMemberLimit(UUID gymId) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        Subscription subscription = findByTier(gym.getSubscriptionTier());
        long memberCount = gymRepository.countMembersByGymId(gymId);

        if (memberCount >= subscription.getMaxMembers()) {
            throw new RuntimeException("Member limit exceeded for this gym.");
        }
    }

    @Override
    public void validateGymLimit(UUID ownerId) {
        List<Gym> gyms = gymRepository.findByOwnerId(ownerId);

        if (!gyms.isEmpty()) {
            Subscription subscription = findByTier(gyms.get(0).getSubscriptionTier());
            if (gyms.size() >= subscription.getMaxGyms()) {
                throw new RuntimeException("Gym limit exceeded for this subscription tier.");
            }
        }
    }

    @Override
    public void upgradeSubscription(UUID gymId, SubscriptionTier subscriptionTier) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        Subscription newSubscription = findByTier(subscriptionTier);
        gym.setSubscriptionTier(newSubscription.getTier());
        gym.setSubscriptionStatus("active");

        gymRepository.save(gym);
    }

    @Override
    public Subscription findByTier(SubscriptionTier tier) {
        return subscriptionRepository.findByTier(tier)
                .orElseThrow(() -> new RuntimeException("Subscription tier not found: " + tier.getTierName()));
    }

    @Override
    public List<SubscriptionDto> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDto getSubscriptionByTier(String tier) {
        Subscription subscription = findByTier(SubscriptionTier.fromString(tier));
        return mapToDto(subscription);
    }

    private SubscriptionDto mapToDto(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setTier(subscription.getTier());
        dto.setMaxMembers(subscription.getMaxMembers());
        dto.setMaxGyms(subscription.getMaxGyms());
        dto.setPrice(subscription.getPrice());
        return dto;
    }


}