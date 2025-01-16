package com.asusoftware.Gym_Management_BE.gym.controller;


import com.asusoftware.Gym_Management_BE.gym.model.dto.CreateGymDto;
import com.asusoftware.Gym_Management_BE.gym.model.dto.GymResponseDto;
import com.asusoftware.Gym_Management_BE.gym.model.dto.UpdateGymDto;
import com.asusoftware.Gym_Management_BE.gym.service.GymService;
import org.springframework.beans.factory.annotation.Autowired;
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
