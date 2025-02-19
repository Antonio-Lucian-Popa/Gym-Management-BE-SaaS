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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gyms")
public class GymController {

    @Autowired
    private GymService gymService;

    /**
     * Creează o sală nouă pentru un admin.
     */
    @PostMapping
    public ResponseEntity<GymResponseDto> createGym(@Valid @RequestBody CreateGymDto createGymDto) {
        GymResponseDto newGym = gymService.createGym(createGymDto);
        return ResponseEntity.ok(newGym);
    }

    /**
     * Obține detaliile unei săli după ID.
     */
    @GetMapping("/{gymId}")
    public ResponseEntity<GymResponseDto> getGymById(@PathVariable UUID gymId) {
        GymResponseDto gym = gymService.getGymById(gymId);
        return ResponseEntity.ok(gym);
    }

    /**
     * Obține toate sălile unui utilizator (admin).
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<GymResponseDto>> getGymsByOwner(@PathVariable UUID ownerId) {
        List<GymResponseDto> gyms = gymService.getGymsByOwner(ownerId);
        return ResponseEntity.ok(gyms);
    }

    /**
     * Obține membrii unei săli după ID-ul sălii.
     */
//    @GetMapping("/{gymId}/members")
//    public ResponseEntity<Page<GymMemberResponseDto>> getMembersByGymId(
//            @PathVariable UUID gymId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "") String sort, // Exemplu: "firstName,asc"
//            @RequestParam(defaultValue = "") String filter // Exemplu: "firstName:John"
//    ) {
//        Page<GymMemberResponseDto> members = gymService.getMembersByGymIdWithFilterAndSort(gymId, page, size, sort, filter);
//        return ResponseEntity.ok(members);
//    }

    @GetMapping("/{gymId}/members")
    public ResponseEntity<Page<GymMemberProjection>> getMembersByGymId(
            @PathVariable UUID gymId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName,asc") String sort,
            @RequestParam(defaultValue = "") String filter) {

        // Construim sortarea din parametrii URL
        String[] sortParams = sort.split(",");
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<GymMemberProjection> members = gymService.getMembersByGymId(gymId, filter, pageable);
        return ResponseEntity.ok(members);
    }


    /**
     * Adaugă un membru într-o sală.
     */
    @PostMapping("/{gymId}/members")
    public ResponseEntity<GymMemberResponseDto> addMemberToGym(
            @PathVariable UUID gymId,
            @Valid @RequestBody CreateGymMemberDto createGymMemberDto) {
        GymMemberResponseDto newMember = gymService.addMemberToGym(gymId, createGymMemberDto);
        return ResponseEntity.ok(newMember);
    }

    /**
     * Actualizează informațiile unui membru dintr-o sală.
     */
    @PutMapping("/{gymId}/members/{memberId}")
    public ResponseEntity<GymMemberResponseDto> updateGymMember(
            @PathVariable UUID gymId,
            @PathVariable UUID memberId,
            @Valid @RequestBody UpdateGymMemberDto updateGymMemberDto) {
        GymMemberResponseDto updatedMember = gymService.updateGymMember(gymId, memberId, updateGymMemberDto);
        return ResponseEntity.ok(updatedMember);
    }

    /**
     * Șterge un membru dintr-o sală.
     */
    @DeleteMapping("/{gymId}/members/{memberId}")
    public ResponseEntity<String> deleteGymMember(@PathVariable UUID gymId, @PathVariable UUID memberId) {
        gymService.deleteGymMember(gymId, memberId);
        return ResponseEntity.ok("Gym member deleted successfully.");
    }


    /**
     * Actualizează informațiile despre o sală.
     */
    @PutMapping("/{gymId}")
    public ResponseEntity<GymResponseDto> updateGym(@PathVariable UUID gymId, @Valid @RequestBody UpdateGymDto updateGymDto) {
        GymResponseDto updatedGym = gymService.updateGym(gymId, updateGymDto);
        return ResponseEntity.ok(updatedGym);
    }

    /**
     * Șterge o sală după ID.
     */
    @DeleteMapping("/{gymId}")
    public ResponseEntity<String> deleteGym(@PathVariable UUID gymId) {
        gymService.deleteGym(gymId);
        return ResponseEntity.ok("Gym deleted successfully.");
    }
}
