package com.loopinback.loopinback.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loopinback.loopinback.dto.PagedResponse;
import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.dto.event.EventRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.UserRepository;
import com.loopinback.loopinback.service.event.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;

    public EventController(EventService eventService, UserRepository userRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EventResponseDTO>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<EventResponseDTO> eventsPage = eventService.getAllEvents(page, size);
        return ResponseEntity.ok(PagedResponse.from(eventsPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<EventResponseDTO> createEvent(
            @Valid @RequestBody EventRequestDTO eventRequestDTO,
            Principal principal) {

        String username = principal.getName();
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EventResponseDTO createdEvent = eventService.createEvent(eventRequestDTO, creator);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO eventRequestDTO,
            Principal principal) {

        eventRequestDTO.setId(id);

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EventResponseDTO updatedEvent = eventService.updateEvent(eventRequestDTO, user);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            Principal principal) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        eventService.deleteEvent(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        List<EventResponseDTO> events = eventService.getEventsByCategory(category, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponseDTO>> searchEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        List<EventResponseDTO> events = eventService.findEventsByFilters(
                category, username, eventName, date, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}/attendances")
    public ResponseEntity<List<AttendanceResponseDTO>> getEventAttendances(
            @PathVariable Long eventId) {
        List<AttendanceResponseDTO> attendances = eventService.getEventAttendances(eventId);
        return ResponseEntity.ok(attendances);
    }

    @GetMapping("/user")
    public ResponseEntity<PagedResponse<EventResponseDTO>> getUserEvents(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Page<EventResponseDTO> eventsPage = eventService.getUserEvents(user, page, size);
        return ResponseEntity.ok(PagedResponse.from(eventsPage));
    }

    @PostMapping("/{eventId}/attend")
    public ResponseEntity<AttendanceResponseDTO> attendEvent(
            @PathVariable Long eventId,
            Principal principal) {

        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AttendanceResponseDTO attendance = eventService.attendEvent(eventId, user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("X-Ticket-Code", attendance.getTicketCode())
                .body(attendance);
    }
}
