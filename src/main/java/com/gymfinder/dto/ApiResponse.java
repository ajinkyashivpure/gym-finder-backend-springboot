package com.gymfinder.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
}