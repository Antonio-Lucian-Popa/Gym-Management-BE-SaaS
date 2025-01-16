package com.asusoftware.Gym_Management_BE.subscription.model.dto;

import com.asusoftware.Gym_Management_BE.subscription.model.SubscriptionTier;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionDto {
    private SubscriptionTier tier;
    private int maxMembers;
    private int maxGyms;
    private BigDecimal price;
}
