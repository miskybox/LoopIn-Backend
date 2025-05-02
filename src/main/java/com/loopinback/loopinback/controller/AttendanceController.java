package com.loopinback.loopinback.controller;
import com.loopinback.loopinback.service.attendance.AttendanceService;
import com.loopinback.loopinback.dto.attendance.AttendanceRequestDTO;
import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.model.Attendance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<Attendance> registerAttendance(
            @RequestBody AttendanceRequestDTO attendanceDTO) {
        Attendance attendance = attendanceService.registerAttendance(
                attendanceDTO.getEventId(),
                attendanceDTO.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    @DeleteMapping("/{eventId}/{userId}")
    public ResponseEntity<Void> unregisterAttendance(
            @PathVariable Long eventId,
            @PathVariable Long userId) {
        attendanceService.unregisterAttendance(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<AttendanceResponseDTO>> getEventAttendances(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(attendanceService.getEventAttendances(eventId));
    }

    @GetMapping("/users/{userId}")
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