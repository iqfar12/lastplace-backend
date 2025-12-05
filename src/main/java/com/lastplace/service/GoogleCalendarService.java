package com.lastplace.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.lastplace.dto.CalendarEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GoogleCalendarService {
    
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Last Place";
    
    public List<CalendarEventDto> getEvents(String calendarId, String apiKey, 
                                           LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Calendar service = getCalendarService(apiKey);
            
            com.google.api.client.util.DateTime timeMin = 
                new com.google.api.client.util.DateTime(
                    java.util.Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant())
                );
            
            com.google.api.client.util.DateTime timeMax = 
                new com.google.api.client.util.DateTime(
                    java.util.Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant())
                );
            
            Events events = service.events().list(calendarId)
                    .setTimeMin(timeMin)
                    .setTimeMax(timeMax)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            
            List<CalendarEventDto> eventDtos = new ArrayList<>();
            
            for (Event event : events.getItems()) {
                eventDtos.add(parseEvent(event));
            }
            
            return eventDtos;
            
        } catch (Exception e) {
            log.error("Error fetching calendar events", e);
            throw new RuntimeException("Failed to fetch calendar events: " + e.getMessage());
        }
    }
    
    private Calendar getCalendarService(String apiKey) throws Exception {        
        return new Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JSON_FACTORY,
            null
        )
        .setApplicationName("lastplace")
        .setGoogleClientRequestInitializer(new CalendarRequestInitializer(apiKey))
        .build();
    }
    
    private CalendarEventDto parseEvent(Event event) {
        log.warn(event.toString());
        String description = event.getDescription() != null ? event.getDescription() : "";
        
        CalendarEventDto.CalendarEventDtoBuilder builder = CalendarEventDto.builder()
                .id(event.getId())
                .title(event.getSummary() != null ? event.getSummary() : "Untitled Event")
                .description(description);
        
        // Parse start and end times
        if (event.getStart() != null && event.getStart().getDateTime() != null) {
            builder.startTime(LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(event.getStart().getDateTime().getValue()),
                ZoneId.systemDefault()
            ));
        }
        
        if (event.getEnd() != null && event.getEnd().getDateTime() != null) {
            builder.endTime(LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(event.getEnd().getDateTime().getValue()),
                ZoneId.systemDefault()
            ));
        }
        
        // Parse custom fields from description
        builder.posterUrl(extractField(description, "\\$poster:\\s*\"([^\"]+)\""));
        builder.ticketLink(extractField(description, "\\$ticketLink:\\s*\"([^\"]+)\""));
        builder.renterName(extractField(description, "\\$renter:\\s*\"([^\"]+)\""));
        builder.contractUrl(extractField(description, "\\$contract:\\s*\"([^\"]+)\""));
        builder.engineers(extractArrayField(description, "\\$engineers:\\s*\\[([^\\]]+)\\]"));
        
        return builder.build();
    }
    
    private String extractField(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    private List<String> extractArrayField(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String arrayContent = matcher.group(1);
            List<String> result = new ArrayList<>();
            
            Pattern itemPattern = Pattern.compile("\"([^\"]+)\"");
            Matcher itemMatcher = itemPattern.matcher(arrayContent);
            
            while (itemMatcher.find()) {
                result.add(itemMatcher.group(1));
            }
            
            return result;
        }
        
        return new ArrayList<>();
    }
}
