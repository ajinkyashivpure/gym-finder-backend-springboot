package com.gymfinder.repository;

import com.gymfinder.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUser_Id(Long userId);
    Optional<UserSubscription> findByUser_IdAndIsActiveTrue(Long userId);
    List<UserSubscription> findBySubscriptionPlan_Gym_Id(Long gymId);
}