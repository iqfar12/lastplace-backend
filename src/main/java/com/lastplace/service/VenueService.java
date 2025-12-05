package com.lastplace.service;

import com.lastplace.dto.SignupRequest;
import com.lastplace.dto.VenueResponse;
import com.lastplace.entity.Venue;
import com.lastplace.repository.VenueRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VenueService {
    
    private final VenueRepository venueRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public Venue createVenue(SignupRequest request) {
        // Check if login ID already exists
        if (venueRepository.existsByLoginId(request.getLoginId())) {
            throw new RuntimeException("Login ID already exists");
        }
        
        // Check if calendar ID already exists
        if (venueRepository.existsByCalendarId(request.getCalendarId())) {
            throw new RuntimeException("Calendar ID already registered");
        }
        
        Venue venue = Venue.builder()
                .venueName(request.getVenueName())
                .loginId(request.getLoginId())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .calendarId(request.getCalendarId())
                .apiKey(request.getApiKey())
                .build();
        
        return venueRepository.save(venue);
    }
    
    public Venue findByLoginId(String loginId) {
        return venueRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
    }
    
    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
    }

    public List<VenueResponse> findAll() {
        List<Venue> venues = venueRepository.findAll();
        List<VenueResponse> result = new ArrayList<>();

        for (Venue venue: venues) {
            result.add(parseResponse(venue));
        }

        return result;
    }

    private VenueResponse parseResponse(Venue venue) {
        return VenueResponse.builder()
        .venueId(venue.getId())
        .venueName(venue.getVenueName())
        .createdAt(venue.getCreatedAt())
        .updatedAt(venue.getUpdatedAt())
        .build();
    }
}
