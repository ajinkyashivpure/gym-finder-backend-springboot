package com.gymfinder.repository;

import com.gymfinder.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByGymId(Long gymId);
}