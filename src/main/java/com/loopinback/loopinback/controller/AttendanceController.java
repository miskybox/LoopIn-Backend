/* 
package com.loopinback.loopinback.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopinback.loopinback.dto.attendance.AttendanceRequestDTO;
import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.service.attendance.AttendanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Attendance> registerAttendance(
            @Valid @RequestBody AttendanceRequestDTO attendanceDTO) {
        Attendance attendance = attendanceService.registerAttendance(
                attendanceDTO.getEventId(), attendanceDTO.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    @DeleteMapping("/{eventId}/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unregisterAttendance(
            @PathVariable Long eventId, @PathVariable Long userId) {
        attendanceService.unregisterAttendance(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<AttendanceResponseDTO>> getEventAttendances(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(attendanceService.getEventAttendances(eventId));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AttendanceResponseDTO>> getUserAttendances(
            @PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.getUserAttendances(userId));
    }

    @GetMapping("/verify/{ticketCode}")
    public ResponseEntity<AttendanceResponseDTO> verifyTicket(
            @PathVariable String ticketCode) {
        return ResponseEntity.ok(attendanceService.verifyTicket(ticketCode));
    }
}
*/

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
@RequestMapping("/api/attendance")
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

        // Get the authenticated user
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

        // Get the authenticated user
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        eventService.unattendEvent(eventId, user);
        return ResponseEntity.noContent().build();
    }
}