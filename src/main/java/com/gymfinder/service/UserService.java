package com.gymfinder.service;

import com.gymfinder.dto.UserDTO;
import com.gymfinder.model.User;
import com.gymfinder.model.UserSubscription;
import com.gymfinder.repository.UserRepository;
import com.gymfinder.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private  UserSubscriptionRepository userSubscriptionRepository;
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }


        if (user.isDeleted()) {
            throw new RuntimeException("User account has been deleted");
        }

        return convertToDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(String email, UserDTO userDTO) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.isDeleted()) {
            throw new RuntimeException("User account has been deleted");
        }

        user.setName(userDTO.getName());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Soft delete user
        user.setDeleted(true);
        user.setVerified(false);
        user.setOtpCode(null);
        user.setOtpExpiration(null);

        // Cancel all active subscriptions

        List<UserSubscription> activeSubscriptions = userSubscriptionRepository
                .findByUser_IdAndIsActiveTrue(user.getId())
                .stream()
                .toList();

        for (UserSubscription subscription : activeSubscriptions) {
            subscription.setActive(false);
            userSubscriptionRepository.save(subscription);
        }

        userRepository.save(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setVerified(user.isVerified());
        return dto;
    }
}