package com.gymfinder.controller;

import com.gymfinder.dto.ApiResponse;
import com.gymfinder.dto.SubscriptionPlanDTO;
import com.gymfinder.model.UserSubscription;
import com.gymfinder.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private  SubscriptionService subscriptionService;

    @GetMapping("/gym/{gymId}/plans")
    public ResponseEntity<?> getGymSubscriptionPlans(@PathVariable Long gymId) {
        List<SubscriptionPlanDTO> plans = subscriptionService.getGymSubscriptionPlans(gymId);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/gym/{gymId}/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSubscriptionPlan(
            @PathVariable Long gymId,
            @RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlanDTO createdPlan = subscriptionService.createSubscriptionPlan(gymId, planDTO);
        return ResponseEntity.ok(createdPlan);
    }

    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<ApiResponse> subscribe(
            Authentication authentication,
            @PathVariable Long planId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        subscriptionService.subscribeUser(userDetails.getUsername(), planId);
        return ResponseEntity.ok(new ApiResponse(true, "Successfully subscribed to the plan"));
    }

    @GetMapping("/my-subscriptions")
    public ResponseEntity<?> getMySubscriptions(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<UserSubscription> subscriptions = subscriptionService.getUserSubscriptions(userDetails.getUsername());
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/cancel/{subscriptionId}")
    public ResponseEntity<ApiResponse> cancelSubscription(
            Authentication authentication,
            @PathVariable Long subscriptionId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        subscriptionService.cancelSubscription(userDetails.getUsername(), subscriptionId);
        return ResponseEntity.ok(new ApiResponse(true, "Subscription cancelled successfully"));
    }
}