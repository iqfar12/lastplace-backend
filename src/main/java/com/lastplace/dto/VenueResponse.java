package com.lastplace.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueResponse {
  private Long venueId;
  private String venueName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
