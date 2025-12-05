package com.lastplace.repository;

import com.lastplace.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByCalendarId(String calendarId);
}
