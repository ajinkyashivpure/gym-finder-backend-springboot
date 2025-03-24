package com.gymfinder.service;

import com.gymfinder.dto.LoginRequest;
import com.gymfinder.dto.SignupRequest;
import com.gymfinder.model.User;
import com.gymfinder.repository.UserRepository;
import com.gymfinder.config.JwtTokenProvider;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class AuthService {

    private static final int OTP_VALIDITY_MINUTES = 10;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private  JwtTokenProvider tokenProvider;
    @Autowired
    private  EmailService emailService;
    @Autowired
    private  CustomUserDetailsService userDetailsService;

    @Transactional
    public String signup(SignupRequest signupRequest) throws MessagingException {
        // Check if user exists and is not deleted
        User existingUser = userRepository.findByEmail(signupRequest.getEmail());
        
        if (existingUser != null && !existingUser.isDeleted()) {
            throw new RuntimeException("Email is already taken");
        }

        User user;
        if (existingUser != null) {
            // Reactivate deleted user
            user = existingUser;
            user.setDeleted(false);
        } else {
            user = new User();
            user.setEmail(signupRequest.getEmail());
        }

        // Update user details
        user.setName(signupRequest.getName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(User.Role.valueOf(signupRequest.getRole().toUpperCase()));
        user.setVerified(false);

        // Generate and set OTP with expiration
        generateAndSetNewOTP(user);

        userRepository.save(user);
        emailService.sendOTPEmail(user.getEmail(), user.getOtpCode());

        return "User registered successfully. Please verify your email with the OTP sent.";
    }

    @Transactional
    public String verifyOTP(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user==null){
            throw new RuntimeException("User not found");
        }

        if (user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        LocalDateTime now = LocalDateTime.now();
        if (user.getOtpExpiration().isBefore(now)) {
            throw new RuntimeException("OTP has expired");
        }

        if (!user.getOtpCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        user.setVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiration(null);
        userRepository.save(user);

        return "User verified successfully";
    }

    @Transactional
    public String resendOTP(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        generateAndSetNewOTP(user);
        userRepository.save(user);

        emailService.sendOTPEmail(email, user.getOtpCode());
        return "New OTP has been sent to your email";
    }

    private void generateAndSetNewOTP(User user) {
        String otp = emailService.generateOTP();
        user.setOtpCode(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.isDeleted()) {
            throw new RuntimeException("User account has been deleted");
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        return tokenProvider.generateToken(userDetails);
    }
}
