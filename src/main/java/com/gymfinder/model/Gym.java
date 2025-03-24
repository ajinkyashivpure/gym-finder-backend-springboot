package com.gymfinder.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "gyms")
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String ownerName;
    private String contactNumber;
    private String address;
    private double latitude;
    private double longitude;

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Equipment> equipment = new HashSet<>();

    @OneToMany(mappedBy = "gym",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubscriptionPlan> subscriptionPlans = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gym)) return false;
        Gym gym = (Gym) o;
        return id != null && id.equals(gym.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}