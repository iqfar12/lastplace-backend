package com.lastplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDto {
    private String id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    
    // Public fields (visible to all)
    private String posterUrl;
    private String ticketLink;
    
    // Private fields (visible only to authenticated venue owners)
    private String renterName;
    private List<String> engineers;
    private String contractUrl;
}
