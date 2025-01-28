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
    @Query("SELECT gm.user.id as id, gm.user.firstName as firstName, gm.user.lastName as lastName, " +
            "gm.user.email as email, gm.membershipType as membershipType, " +
            "gm.membershipStatus as membershipStatus, gm.startDate as startDate, gm.endDate as endDate " +
            "FROM GymMember gm WHERE gm.gym.id = :gymId " +
            "AND (LOWER(gm.user.firstName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(gm.user.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(gm.user.email) LIKE LOWER(CONCAT('%', :filter, '%')))")
    Page<GymMemberProjection> findMembersByGymIdAndFilter(@Param("gymId") UUID gymId,
                                                          @Param("filter") String filter,
                                                          Pageable pageable);



    Optional<GymMember> findByIdAndGymId(UUID memberId, UUID gymId);
}
