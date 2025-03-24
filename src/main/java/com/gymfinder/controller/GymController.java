package com.gymfinder.controller;

import com.gymfinder.dto.ApiResponse;
import com.gymfinder.dto.GymDTO;
import com.gymfinder.service.GymService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gyms")
public class GymController {
    @Autowired
    private  GymService gymService;

    @GetMapping
    public ResponseEntity<?> getAllGyms() {
        List<GymDTO> gyms = gymService.getAllGyms();
        return ResponseEntity.ok(gyms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymDTO> getGymById(@PathVariable Long id) {
        GymDTO gym = gymService.getGymById(id);
        return ResponseEntity.ok(gym);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createGym(@RequestBody GymDTO gymDTO) {
        GymDTO createdGym = gymService.createGym(gymDTO);
        return ResponseEntity.ok(createdGym);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGym(@PathVariable Long id, @RequestBody GymDTO gymDTO) {
        GymDTO updatedGym = gymService.updateGym(id, gymDTO);
        return ResponseEntity.ok( updatedGym);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteGym(@PathVariable Long id) {
        gymService.deleteGym(id);
        return ResponseEntity.ok(new ApiResponse(true, "Gym deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchGyms(@RequestParam String name) {
        List<GymDTO> gyms = gymService.searchGyms(name);
        return ResponseEntity.ok(gyms);
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> findNearbyGyms(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius) {
        List<GymDTO> gyms = gymService.findNearbyGyms(latitude, longitude, radius);
        return ResponseEntity.ok(gyms);
    }
}