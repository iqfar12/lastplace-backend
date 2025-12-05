package com.lastplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    
    @NotBlank(message = "Venue name is required")
    @Size(max = 100, message = "Venue name must be less than 100 characters")
    private String venueName;
    
    @NotBlank(message = "Login ID is required")
    @Size(min = 4, max = 50, message = "Login ID must be between 4 and 50 characters")
    private String loginId;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Calendar ID is required")
    private String calendarId;
    
    @NotBlank(message = "API Key is required")
    private String apiKey;
}
