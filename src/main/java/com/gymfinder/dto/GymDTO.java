package com.gymfinder.dto;

import lombok.Data;
import java.util.Set;

@Data
public class GymDTO {
    private Long id;
    private String name;
    private String ownerName;
    private String contactNumber;
    private String address;
    private double latitude;
    private double longitude;
    private Set<EquipmentDTO> equipment;
    private Set<TrainerDTO> trainers;
    private Set<SubscriptionPlanDTO> subscriptionPlans;
}

