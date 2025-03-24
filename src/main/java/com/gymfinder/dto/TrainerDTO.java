package com.gymfinder.dto;

import lombok.Data;

@Data
public class TrainerDTO {
    private Long id;
    private String name;
    private String specialization;
    private String contactNumber;
    private String imageUrl;
}