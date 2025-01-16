package com.asusoftware.Gym_Management_BE.subscription.model;

public enum SubscriptionTier {
    BASIC("Basic", 100, 1, 0.0),
    PRO("Pro", 500, 3, 49.99),
    ENTERPRISE("Enterprise", Integer.MAX_VALUE, Integer.MAX_VALUE, 199.99);

    private final String tierName;
    private final int maxMembers;
    private final int maxGyms;
    private final double price;

    SubscriptionTier(String tierName, int maxMembers, int maxGyms, double price) {
        this.tierName = tierName;
        this.maxMembers = maxMembers;
        this.maxGyms = maxGyms;
        this.price = price;
    }

    public String getTierName() {
        return tierName;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getMaxGyms() {
        return maxGyms;
    }

    public double getPrice() {
        return price;
    }

    public static SubscriptionTier fromString(String tierName) {
        for (SubscriptionTier tier : SubscriptionTier.values()) {
            if (tier.getTierName().equalsIgnoreCase(tierName)) {
                return tier;
            }
        }
        throw new IllegalArgumentException("Invalid subscription tier: " + tierName);
    }
}
