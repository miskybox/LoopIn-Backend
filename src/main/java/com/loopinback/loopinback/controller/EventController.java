/* 
package com.loopinback.loopinback.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.loopinback.loopinback.service.event.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Endpoint público para mostrar todos los eventos
    @GetMapping
    public ResponseEntity<PagedResponse<EventResponseDTO>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(
                PagedResponse.from(eventService.getAllEvents(page, size)));
    }

    // Endpoint público para buscar eventos por filtros
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<EventResponseDTO>> findEventsByFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        LocalDate parsedDate = null;
        if (date != null && !date.isEmpty()) {
            try {
                parsedDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        List<EventResponseDTO> events = eventService.findEventsByFilters(
                category, username, eventName, parsedDate, page, size);

        // Crear manualmente un PagedResponse ya que findEventsByFilters devuelve List,
        // no Page
        PagedResponse<EventResponseDTO> pagedResponse = new PagedResponse<>(
                events,
                page,
                size,
                events.size(), // Esto es una aproximación; idealmente, necesitarías el total exacto
                (int) Math.ceil((double) events.size() / size),
                events.size() <= page * size);

        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventResponseDTO> createEvent(

            @Valid @RequestBody EventRequestDTO eventDTO,

            @AuthenticationPrincipal User creator) {
        EventResponseDTO createdEvent = eventService.createEvent(eventDTO, creator);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO eventDTO,
            @AuthenticationPrincipal User user) {
        eventDTO.setId(id);
        EventResponseDTO updatedEvent = eventService.updateEvent(eventDTO, user);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        eventService.deleteEvent(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/attendances")
    public ResponseEntity<List<AttendanceResponseDTO>> getEventAttendances(
            @PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventAttendances(id));
    }

    @PostMapping("/{id}/attend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AttendanceResponseDTO> attendEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(eventService.attendEvent(id, user));
    }

    @DeleteMapping("/{id}/attend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unattendEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        eventService.unattendEvent(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user-events")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<EventResponseDTO>> getUserEvents(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(
                PagedResponse.from(eventService.getUserEvents(user, page, size)));
    }
}

*/

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
            @RequestParam(defaultValue = "14") int size) {
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

        // Get the authenticated user from the security context
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

        // Set the ID from the path variable
        eventRequestDTO.setId(id);

        // Get the authenticated user
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

        // Get the authenticated user
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
            @RequestParam(defaultValue = "14") int size) {
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
            @RequestParam(defaultValue = "14") int size) {
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
            @RequestParam(defaultValue = "14") int size) {

        // Get the authenticated user
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Page<EventResponseDTO> eventsPage = eventService.getUserEvents(user, page, size);
        return ResponseEntity.ok(PagedResponse.from(eventsPage));
    }
}