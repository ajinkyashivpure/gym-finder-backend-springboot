package com.gymfinder.repository;

import com.gymfinder.model.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
    List<Gym> findByNameContainingIgnoreCase(String name);

    @Query("SELECT g FROM Gym g WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(g.latitude)) * " +
            "cos(radians(g.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(g.latitude)))) < :radius")
    List<Gym> findGymsWithinRadius(double latitude, double longitude, double radius);
}