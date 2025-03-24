package com.gymfinder.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int durationInMonths;
}
