package com.asusoftware.Gym_Management_BE.gym.repository;

import com.asusoftware.Gym_Management_BE.gym.model.GymMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, UUID> {
    List<GymMember> findByGymId(UUID gymId);
    Optional<GymMember> findByIdAndGymId(UUID memberId, UUID gymId);
}
