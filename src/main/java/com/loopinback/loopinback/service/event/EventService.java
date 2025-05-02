package com.loopinback.loopinback.service.event;
import java.time.LocalDate;
import java.util.List;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.dto.event.EventRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.model.User;

public interface EventService {
    List<EventResponseDTO> getAllEvents();

    EventResponseDTO getEventById(Long id);

    EventResponseDTO createEvent(EventRequestDTO eventDTO, User creator);

    EventResponseDTO updateEvent(EventRequestDTO eventDTO);

    void deleteEvent(Long id);

    List<AttendanceResponseDTO> getEventAttendances(Long eventId);

    List<EventResponseDTO> getEventsByCategory(String category, int page, int size);

    List<EventResponseDTO> findEventsByFilters(
            String category,
            String username,
            String eventName,
            LocalDate date,
            int page,
            int size);
}

