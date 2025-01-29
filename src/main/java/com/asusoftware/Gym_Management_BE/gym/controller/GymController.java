package com.asusoftware.Gym_Management_BE.gym.controller;


import com.asusoftware.Gym_Management_BE.gym.model.dto.*;
import com.asusoftware.Gym_Management_BE.gym.service.GymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/gyms")
public class GymController {

    @Autowired
    private GymService gymService;

    /**
     * 📌 Creează o sală nouă pentru un admin.
     */
    @PostMapping
    public ResponseEntity<GymResponseDto> createGym(@Valid @RequestBody CreateGymDto createGymDto) {
        return ResponseEntity.ok(gymService.createGym(createGymDto));
    }

    /**
     * 📌 Obține detaliile unei săli după ID.
     */
    @GetMapping("/{gymId}")
    public ResponseEntity<GymResponseDto> getGymById(@PathVariable UUID gymId) {
        return ResponseEntity.ok(gymService.getGymById(gymId));
    }

    /**
     * 📌 Obține toate sălile unui utilizator (admin).
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<GymResponseDto>> getGymsByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(gymService.getGymsByOwner(ownerId));
    }

    /**
     * 📌 Obține membrii unei săli după ID-ul sălii, cu paginare, filtrare și sortare.
     */
    @GetMapping("/{gymId}/members")
    public ResponseEntity<PagedResponse<GymMemberProjection>> getMembersByGymId(
            @PathVariable UUID gymId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String filter
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        PagedResponse<GymMemberProjection> response = gymService.getMembersByGymId(gymId, filter, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 📌 Adaugă un membru într-o sală.
     */
    @PostMapping("/{gymId}/members")
    public ResponseEntity<GymMemberResponseDto> addMemberToGym(
            @PathVariable UUID gymId,
            @Valid @RequestBody CreateGymMemberDto createGymMemberDto) {
        return ResponseEntity.ok(gymService.addMemberToGym(gymId, createGymMemberDto));
    }

    /**
     * 📌 Actualizează informațiile unui membru dintr-o sală.
     */
    @PutMapping("/{gymId}/members/{memberId}")
    public ResponseEntity<GymMemberResponseDto> updateGymMember(
            @PathVariable UUID gymId,
            @PathVariable UUID memberId,
            @Valid @RequestBody UpdateGymMemberDto updateGymMemberDto) {
        return ResponseEntity.ok(gymService.updateGymMember(gymId, memberId, updateGymMemberDto));
    }

    /**
     * 📌 Șterge un membru dintr-o sală.
     */
    @DeleteMapping("/{gymId}/members/{memberId}")
    public ResponseEntity<String> deleteGymMember(@PathVariable UUID gymId, @PathVariable UUID memberId) {
        gymService.deleteGymMember(gymId, memberId);
        return ResponseEntity.ok("Gym member deleted successfully.");
    }

    /**
     * 📌 Actualizează informațiile despre o sală.
     */
    @PutMapping("/{gymId}")
    public ResponseEntity<GymResponseDto> updateGym(@PathVariable UUID gymId, @Valid @RequestBody UpdateGymDto updateGymDto) {
        return ResponseEntity.ok(gymService.updateGym(gymId, updateGymDto));
    }

    /**
     * 📌 Șterge o sală după ID.
     */
    @DeleteMapping("/{gymId}")
    public ResponseEntity<String> deleteGym(@PathVariable UUID gymId) {
        gymService.deleteGym(gymId);
        return ResponseEntity.ok("Gym deleted successfully.");
    }

    /**
     * 📌 Converteste parametrii de sortare într-un obiect Sort, gestionând erorile.
     */
    /**
     * 🛠 Converteste parametrii de sortare într-un obiect Sort.
     */
    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.unsorted();
        }

        try {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                String field = parts[0];

                // Convertim user.firstName → user.firstName pentru entitatea GymMember
                if (field.equals("firstName")) field = "user.firstName";
                else if (field.equals("lastName")) field = "user.lastName";
                else if (field.equals("email")) field = "user.email";

                return Sort.by(Sort.Direction.fromString(parts[1].toUpperCase()), field);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid sort format: " + sort);
        }

        return Sort.unsorted();
    }
}
