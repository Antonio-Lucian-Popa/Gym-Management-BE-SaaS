package com.asusoftware.Gym_Management_BE.payment;

import com.asusoftware.Gym_Management_BE.payment.model.MemberPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<MemberPayment, UUID> {
    List<MemberPayment> findByMemberId(UUID memberId);
}