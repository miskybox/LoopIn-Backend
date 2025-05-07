package com.loopinback.loopinback.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopinback.loopinback.dto.attendance.AttendanceRequestDTO;
import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.UserRepository;
import com.loopinback.loopinback.service.attendance.AttendanceService;
import com.loopinback.loopinback.service.event.EventService;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final EventService eventService;
    private final UserRepository userRepository;
    private final AttendanceService attendanceService;

    public AttendanceController(
            EventService eventService,
            UserRepository userRepository,
            AttendanceService attendanceService) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.attendanceService = attendanceService;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<AttendanceResponseDTO> attendEvent(
            @PathVariable Long eventId,
            @RequestBody AttendanceRequestDTO attendanceRequestDTO,
            @AuthenticationPrincipal User currentUser) {
        attendanceRequestDTO.setEventId(eventId);
        Attendance attendance = attendanceService.registerAttendance(
                eventId,
                currentUser.getId());
        return ResponseEntity.ok(convertToResponseDTO(attendance));
    }

    private AttendanceResponseDTO convertToResponseDTO(Attendance attendance) {
        AttendanceResponseDTO responseDTO = new AttendanceResponseDTO();
        responseDTO.setId(attendance.getId());
        responseDTO.setEventId(attendance.getEvent().getId());
        responseDTO.setUserId(attendance.getUser().getId());
        return responseDTO;
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
