package com.loopinback.loopinback.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.UserRepository;
import com.loopinback.loopinback.service.event.EventService;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    private final EventService eventService;
    private final UserRepository userRepository;

    public AttendanceController(EventService eventService, UserRepository userRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<AttendanceResponseDTO> attendEvent(
            @PathVariable Long eventId,
            Principal principal) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AttendanceResponseDTO attendance = eventService.attendEvent(eventId, user);
        return new ResponseEntity<>(attendance, HttpStatus.CREATED);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> unattendEvent(
            @PathVariable Long eventId,
            Principal principal) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        eventService.unattendEvent(eventId, user);
        return ResponseEntity.noContent().build();
    }
}