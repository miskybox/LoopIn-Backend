package com.loopinback.loopinback.service.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.dto.event.EventRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.model.Category;
import com.loopinback.loopinback.model.Event;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.AttendanceRepository;
import com.loopinback.loopinback.repository.EventRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AttendanceRepository attendanceRepository;

    public EventServiceImpl(EventRepository eventRepository,

            AttendanceRepository attendanceRepository) {
        this.eventRepository = eventRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        return convertToDto(event);
    }

    @Override
    public EventResponseDTO createEvent(EventRequestDTO dto, User creator) {
        if (creator == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere creador");
        }

        Event event = new Event();
        populateEventFromDto(event, dto);
        event.setCreator(creator);

        Event savedEvent = eventRepository.save(event);
        return convertToDto(savedEvent);
    }

    @Override
    public EventResponseDTO updateEvent(EventRequestDTO dto) {
        Event event = eventRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

        populateEventFromDto(event, dto);
        Event updated = eventRepository.save(event);

        return convertToDto(updated);
    }

    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado");
        }
        eventRepository.deleteById(id);
    }

    @Override
    public List<AttendanceResponseDTO> getEventAttendances(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado");
        }

        List<Attendance> attendances = attendanceRepository.findByEventId(eventId);

        return attendances.stream()
                .map(a -> new AttendanceResponseDTO(
                        a.getId(),
                        a.getUser().getId(),
                        a.getUser().getUsername(),
                        a.getEvent().getId(),
                        a.getTicketCode()))
                .toList();
    }

    @Override
    public List<EventResponseDTO> getEventsByCategory(String categoryStr, int page, int size) {
        Category category;

        try {
            category = Category.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría inválida");
        }

        Pageable pageable = PageRequest.of(page, size);

        return eventRepository.findByCategory(category, pageable)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<EventResponseDTO> findEventsByFilters(String categoryStr, String username, String eventName,
            LocalDate date, int page, int size) {
        Category category = null;
        if (categoryStr != null && !categoryStr.isEmpty()) {
            try {
                category = Category.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría inválida");
            }
        }

        LocalDateTime startDate = (date != null) ? date.atStartOfDay() : null;

        Pageable pageable = PageRequest.of(page, size);

        return eventRepository.findByFilters(category, eventName, startDate, username, pageable)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private EventResponseDTO convertToDto(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getCategory(),
                event.getCreator().getUsername());
    }

    private void populateEventFromDto(Event event, EventRequestDTO dto) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setLocation(dto.getLocation());

        try {

            String categoryStr = dto.getCategory().name().toUpperCase(Locale.ROOT);
            Category category = Category.valueOf(categoryStr);
            event.setCategory(category);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de categoría no válido");
        }
    }
}
