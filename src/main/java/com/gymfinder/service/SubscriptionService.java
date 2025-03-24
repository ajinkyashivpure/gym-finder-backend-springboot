package com.gymfinder.service;

import com.gymfinder.dto.SubscriptionPlanDTO;
import com.gymfinder.model.Gym;
import com.gymfinder.model.SubscriptionPlan;
import com.gymfinder.model.User;
import com.gymfinder.model.UserSubscription;
import com.gymfinder.repository.GymRepository;
import com.gymfinder.repository.SubscriptionPlanRepository;
import com.gymfinder.repository.UserRepository;
import com.gymfinder.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    @Autowired
    private  SubscriptionPlanRepository subscriptionPlanRepository;
    @Autowired
    private  UserSubscriptionRepository userSubscriptionRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  GymRepository gymRepository;

    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getGymSubscriptionPlans(Long gymId) {
        return subscriptionPlanRepository.findByGymId(gymId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubscriptionPlanDTO createSubscriptionPlan(Long gymId, SubscriptionPlanDTO planDTO) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setGym(gym);
        updatePlanFromDTO(plan, planDTO);

        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return convertToDTO(savedPlan);
    }

    @Transactional
    public void subscribeUser(String email, Long planId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found"));

        // Check if user already has an active subscription
        userSubscriptionRepository.findByUser_IdAndIsActiveTrue(user.getId())
                .ifPresent(s -> {
                    throw new RuntimeException("User already has an active subscription");
                });

        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setSubscriptionPlan(plan);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(plan.getDurationInMonths()));
        subscription.setActive(true);

        userSubscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<UserSubscription> getUserSubscriptions(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return userSubscriptionRepository.findByUser_Id(user.getId());
    }

    @Transactional
    public void cancelSubscription(String email, Long subscriptionId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        UserSubscription subscription = userSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to subscription");
        }

        subscription.setActive(false);
        userSubscriptionRepository.save(subscription);
    }

    private SubscriptionPlanDTO convertToDTO(SubscriptionPlan plan) {
        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setDurationInMonths(plan.getDurationInMonths());
        return dto;
    }

    private void updatePlanFromDTO(SubscriptionPlan plan, SubscriptionPlanDTO dto) {
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setDurationInMonths(dto.getDurationInMonths());
    }
}