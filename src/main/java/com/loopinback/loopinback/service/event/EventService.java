package com.loopinback.loopinback.service.event;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.dto.event.EventRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.model.User;

public interface EventService {
    List<EventResponseDTO> getAllEvents();

    Page<EventResponseDTO> getAllEvents(int page, int size);

    EventResponseDTO getEventById(Long id);

    EventResponseDTO createEvent(EventRequestDTO eventDTO, User creator);

    EventResponseDTO updateEvent(EventRequestDTO eventDTO, User user);

    EventResponseDTO updateEvent(EventRequestDTO eventDTO);

    void deleteEvent(Long id);

    void deleteEvent(Long id, User user);

    List<AttendanceResponseDTO> getEventAttendances(Long eventId);

    List<EventResponseDTO> getEventsByCategory(String category, int page, int size);

    List<EventResponseDTO> findEventsByFilters(
            String category,
            String username,
            String eventName,
            LocalDate date,
            int page,
            int size);

    Page<EventResponseDTO> getUserEvents(User user, int page, int size);

    AttendanceResponseDTO attendEvent(Long eventId, User user);

    void unattendEvent(Long eventId, User user);

    List<AttendanceResponseDTO> getUserAttendances(Long userId);
}
