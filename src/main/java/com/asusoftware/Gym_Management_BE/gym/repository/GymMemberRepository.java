package com.asusoftware.Gym_Management_BE.gym.repository;

import com.asusoftware.Gym_Management_BE.gym.model.GymMember;
import com.asusoftware.Gym_Management_BE.gym.model.dto.GymMemberProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, UUID>, JpaSpecificationExecutor<GymMember> {
    List<GymMember> findByGymId(UUID gymId);
    @Query("""
        SELECT g.id as id,
               u.firstName as firstName,
               u.lastName as lastName,
               u.email as email,
               u.phone as phoneNumber, 
               g.membershipType as membershipType,
               g.membershipStatus as membershipStatus,
               g.startDate as startDate,
               g.endDate as endDate
        FROM GymMember g
        JOIN g.user u
        WHERE g.gym.id = :gymId
        AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :filter, '%')) 
          OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%'))
          OR LOWER(u.email) LIKE LOWER(CONCAT('%', :filter, '%')))
    """)
    Page<GymMemberProjection> findMembersByGymIdAndFilter(@Param("gymId") UUID gymId, @Param("filter") String filter, Pageable pageable);



    Optional<GymMember> findByIdAndGymId(UUID memberId, UUID gymId);
}
