package com.asusoftware.Gym_Management_BE.gym.repository;

import com.asusoftware.Gym_Management_BE.gym.model.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GymRepository extends JpaRepository<Gym, UUID> {
    List<Gym> findByOwnerId(UUID ownerId);

    @Query("SELECT COUNT(m) FROM GymMember m WHERE m.gym.id = :gymId")
    long countMembersByGymId(UUID gymId);
}
