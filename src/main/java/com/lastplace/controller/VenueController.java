package com.lastplace.controller;

import com.lastplace.dto.CalendarEventDto;
import com.lastplace.dto.VenueResponse;
import com.lastplace.entity.Venue;
import com.lastplace.security.JwtTokenProvider;
import com.lastplace.service.GoogleCalendarService;
import com.lastplace.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    
    private final VenueService venueService;
    private final GoogleCalendarService googleCalendarService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/{venueId}/events")
    public ResponseEntity<List<CalendarEventDto>> getEvents(
            @PathVariable Long venueId,
            @RequestParam(required = false) String month,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            Venue venue = venueService.findById(venueId);
            
            // Parse month parameter or use current month
            LocalDateTime startDate;
            LocalDateTime endDate;
            
            if (month != null && month.matches("\\d{4}-\\d{2}")) {
                String[] parts = month.split("-");
                int year = Integer.parseInt(parts[0]);
                int monthValue = Integer.parseInt(parts[1]);
                startDate = LocalDateTime.of(year, monthValue, 1, 0, 0);
                endDate = startDate.plusMonths(1);
            } else {
                startDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
                endDate = startDate.plusMonths(1);
            }
            
            List<CalendarEventDto> events = googleCalendarService.getEvents(
                    venue.getCalendarId(),
                    venue.getApiKey(),
                    startDate,
                    endDate
            );
            
            // Check if user is authenticated
            boolean isAuthenticated = false;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    Long authenticatedVenueId = jwtTokenProvider.getVenueId(token);
                    isAuthenticated = authenticatedVenueId.equals(venueId);
                }
            }
            
            // Filter private fields if not authenticated
            if (!isAuthenticated) {
                events.forEach(event -> {
                    event.setRenterName(null);
                    event.setEngineers(null);
                    event.setContractUrl(null);
                });
            }
            
            return ResponseEntity.ok(events);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{venueId}")
    public ResponseEntity<VenueResponse> getVenueInfo(@PathVariable Long venueId) {
        try {
            Venue venue = venueService.findById(venueId);
            return ResponseEntity.ok(
                VenueResponse.builder()
                .venueId(venue.getId())
                .venueName(venue.getVenueName())
                .createdAt(venue.getCreatedAt())
                .updatedAt(venue.getUpdatedAt())
                .build()
            );
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<VenueResponse>> getVenueList() {
        try {
            List<VenueResponse> venues = venueService.findAll();
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}
