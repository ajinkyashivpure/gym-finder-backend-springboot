package com.gymfinder.service;

import com.gymfinder.dto.EquipmentDTO;
import com.gymfinder.dto.GymDTO;
import com.gymfinder.dto.SubscriptionPlanDTO;
import com.gymfinder.dto.TrainerDTO;
import com.gymfinder.model.*;
import com.gymfinder.repository.GymRepository;
import com.gymfinder.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GymService {
    @Autowired
    private  GymRepository gymRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;


    public List<GymDTO> getAllGyms() {
        return gymRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GymDTO getGymById(Long id) {
        return gymRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Gym not found"));
    }


    public GymDTO createGym(GymDTO gymDTO) {
        System.out.println("Received GymDTO: " + gymDTO);
        System.out.println("Equipment: " + gymDTO.getEquipment());
        System.out.println("Trainers: " + gymDTO.getTrainers());
        System.out.println("Plans: " + gymDTO.getSubscriptionPlans());
        Gym gym = new Gym();
        updateGymFields(gym, gymDTO);

        if (gymDTO.getEquipment() != null) {
            gym.setEquipment(gymDTO.getEquipment().stream()
                    .map(eqDTO -> createEquipment(eqDTO, gym))
                    .collect(Collectors.toSet()));
        }

        if (gymDTO.getTrainers() != null) {
            gym.setTrainers(gymDTO.getTrainers().stream()
                    .map(tDTO -> createTrainer(tDTO, gym))
                    .collect(Collectors.toSet()));
        }

        if (gymDTO.getSubscriptionPlans() != null) {
            gym.setSubscriptionPlans(gymDTO.getSubscriptionPlans().stream()
                    .map(spDTO -> createSubscriptionPlan(spDTO, gym))
                    .collect(Collectors.toSet()));
        }

        return convertToDTO(gymRepository.save(gym));
    }

    private Equipment createEquipment(EquipmentDTO dto, Gym gym) {
        Equipment equipment = new Equipment();
        equipment.setName(dto.getName());
        equipment.setDescription(dto.getDescription());
        equipment.setImageUrl(dto.getImageUrl());
        equipment.setGym(gym);
        return equipment;
    }

    private Trainer createTrainer(TrainerDTO dto, Gym gym) {
        Trainer trainer = new Trainer();
        trainer.setName(dto.getName());
        trainer.setSpecialization(dto.getSpecialization());
        trainer.setContactNumber(dto.getContactNumber());
        trainer.setImageUrl(dto.getImageUrl());
        trainer.setGym(gym);
        return trainer;
    }

    private SubscriptionPlan createSubscriptionPlan(SubscriptionPlanDTO dto, Gym gym) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setDurationInMonths(dto.getDurationInMonths());
        plan.setGym(gym);
        return plan;
    }

    private void updateGymFields(Gym gym, GymDTO dto) {
        gym.setName(dto.getName());
        gym.setOwnerName(dto.getOwnerName());
        gym.setContactNumber(dto.getContactNumber());
        gym.setAddress(dto.getAddress());
        gym.setLatitude(dto.getLatitude());
        gym.setLongitude(dto.getLongitude());
    }

    private GymDTO convertToDTO(Gym gym) {
        GymDTO dto = new GymDTO();
        dto.setId(gym.getId());
        dto.setName(gym.getName());
        dto.setOwnerName(gym.getOwnerName());
        dto.setContactNumber(gym.getContactNumber());
        dto.setAddress(gym.getAddress());
        dto.setLatitude(gym.getLatitude());
        dto.setLongitude(gym.getLongitude());

        dto.setEquipment(gym.getEquipment().stream()
                .map(this::convertToEquipmentDTO)
                .collect(Collectors.toSet()));

        dto.setTrainers(gym.getTrainers().stream()
                .map(this::convertToTrainerDTO)
                .collect(Collectors.toSet()));

        dto.setSubscriptionPlans(gym.getSubscriptionPlans().stream()
                .map(this::convertToSubscriptionPlanDTO)
                .collect(Collectors.toSet()));

        return dto;
    }



    @Transactional
    public GymDTO updateGym(Long id, GymDTO gymDTO) {
        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        // Update basic fields
        gym.setName(gymDTO.getName());
        gym.setOwnerName(gymDTO.getOwnerName());
        gym.setContactNumber(gymDTO.getContactNumber());
        gym.setAddress(gymDTO.getAddress());
        gym.setLatitude(gymDTO.getLatitude());
        gym.setLongitude(gymDTO.getLongitude());

        // Clear and update equipment
        gym.getEquipment().clear();
        if (gymDTO.getEquipment() != null) {
            gymDTO.getEquipment().forEach(eqDTO -> {
                Equipment equipment = new Equipment();
                equipment.setName(eqDTO.getName());
                equipment.setDescription(eqDTO.getDescription());
                equipment.setImageUrl(eqDTO.getImageUrl());
                equipment.setGym(gym);
                gym.getEquipment().add(equipment);
            });
        }

        // Clear and update trainers
        gym.getTrainers().clear();
        if (gymDTO.getTrainers() != null) {
            gymDTO.getTrainers().forEach(trainerDTO -> {
                Trainer trainer = new Trainer();
                trainer.setName(trainerDTO.getName());
                trainer.setSpecialization(trainerDTO.getSpecialization());
                trainer.setContactNumber(trainerDTO.getContactNumber());
                trainer.setImageUrl(trainerDTO.getImageUrl());
                trainer.setGym(gym);
                gym.getTrainers().add(trainer);
            });
        }

        // Handle subscription plans update
        // Handle subscription plans update
        if (gymDTO.getSubscriptionPlans() != null) {
            // Get existing plans that have active subscriptions
            List<Long> plansWithSubscriptions = userSubscriptionRepository
                    .findBySubscriptionPlan_Gym_Id(id)
                    .stream()
                    .map(us -> us.getSubscriptionPlan().getId())
                    .collect(Collectors.toList());

            // Remove plans that don't have active subscriptions
            gym.getSubscriptionPlans().removeIf(plan -> !plansWithSubscriptions.contains(plan.getId()));

            // Create a set to track processed plans
            Set<String> processedPlanNames = new HashSet<>();

            // Update or add plans
            gymDTO.getSubscriptionPlans().forEach(planDTO -> {
                // Skip if we already processed a plan with this name
                if (!processedPlanNames.add(planDTO.getName())) {
                    return;
                }

                // Check if a plan with this name already exists
                Optional<SubscriptionPlan> existingPlan = gym.getSubscriptionPlans().stream()
                        .filter(p -> p.getName().equals(planDTO.getName()))
                        .findFirst();

                if (existingPlan.isPresent()) {
                    // Update existing plan
                    SubscriptionPlan plan = existingPlan.get();
                    plan.setDescription(planDTO.getDescription());
                    plan.setPrice(planDTO.getPrice());
                    plan.setDurationInMonths(planDTO.getDurationInMonths());
                } else {
                    // Add new plan
                    SubscriptionPlan newPlan = new SubscriptionPlan();
                    newPlan.setName(planDTO.getName());
                    newPlan.setDescription(planDTO.getDescription());
                    newPlan.setPrice(planDTO.getPrice());
                    newPlan.setDurationInMonths(planDTO.getDurationInMonths());
                    newPlan.setGym(gym);
                    gym.getSubscriptionPlans().add(newPlan);
                }
            });
        }

        Gym updatedGym = gymRepository.save(gym);
        return convertToDTO(updatedGym);
    }


    @Transactional
    public void deleteGym(Long id) {
        if (!gymRepository.existsById(id)) {
            throw new RuntimeException("Gym not found");
        }
        gymRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GymDTO> searchGyms(String name) {
        return gymRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GymDTO> findNearbyGyms(double latitude, double longitude, double radiusInKm) {
        return gymRepository.findGymsWithinRadius(latitude, longitude, radiusInKm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private EquipmentDTO convertToEquipmentDTO(Equipment equipment) {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(equipment.getId());
        dto.setName(equipment.getName());
        dto.setDescription(equipment.getDescription());
        dto.setImageUrl(equipment.getImageUrl());
        return dto;
    }

    private TrainerDTO convertToTrainerDTO(Trainer trainer) {
        TrainerDTO dto = new TrainerDTO();
        dto.setId(trainer.getId());
        dto.setName(trainer.getName());
        dto.setSpecialization(trainer.getSpecialization());
        dto.setContactNumber(trainer.getContactNumber());
        dto.setImageUrl(trainer.getImageUrl());
        return dto;
    }

    private SubscriptionPlanDTO convertToSubscriptionPlanDTO(SubscriptionPlan plan) {
        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setDurationInMonths(plan.getDurationInMonths());
        return dto;
    }


    private void updateGymFromDTO(Gym gym, GymDTO dto) {
        gym.setName(dto.getName());
        gym.setOwnerName(dto.getOwnerName());
        gym.setContactNumber(dto.getContactNumber());
        gym.setAddress(dto.getAddress());
        gym.setLatitude(dto.getLatitude());
        gym.setLongitude(dto.getLongitude());
    }
}