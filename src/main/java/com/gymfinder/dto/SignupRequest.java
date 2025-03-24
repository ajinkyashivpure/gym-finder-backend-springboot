package com.gymfinder.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String role;
}