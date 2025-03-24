package com.gymfinder.controller;

import com.gymfinder.dto.ApiResponse;
import com.gymfinder.dto.UserDTO;
import com.gymfinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private  UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDTO userDTO = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody UserDTO userDTO) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDTO updatedUser = userService.updateProfile(userDetails.getUsername(), userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteAccount(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        userService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Account deleted successfully"));
    }
}