package com.lastplace.controller;

import com.lastplace.dto.AuthResponse;
import com.lastplace.dto.LoginRequest;
import com.lastplace.dto.SignupRequest;
import com.lastplace.entity.Venue;
import com.lastplace.security.JwtTokenProvider;
import com.lastplace.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final VenueService venueService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        try {
            Venue venue = venueService.createVenue(request);
            
            String token = jwtTokenProvider.createToken(venue.getId(), venue.getLoginId());
            
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .venueId(venue.getId())
                    .venueName(venue.getVenueName())
                    .message("Venue registered successfully")
                    .build());
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Venue venue = venueService.findByLoginId(request.getLoginId());
            
            if (!passwordEncoder.matches(request.getPassword(), venue.getPasswordHash())) {
                return ResponseEntity.badRequest()
                        .body(AuthResponse.builder()
                                .message("Invalid credentials")
                                .build());
            }
            
            String token = jwtTokenProvider.createToken(venue.getId(), venue.getLoginId());
            
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .venueId(venue.getId())
                    .venueName(venue.getVenueName())
                    .message("Login successful")
                    .build());
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .message("Invalid credentials")
                            .build());
        }
    }
}
