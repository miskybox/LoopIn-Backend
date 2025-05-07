package com.loopinback.loopinback.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.UserRepository;
import com.loopinback.loopinback.service.attendance.AttendanceService;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final UserRepository userRepository;
    private final AttendanceService attendanceService;

    public AttendanceController(
            UserRepository userRepository,
            AttendanceService attendanceService) {
        this.userRepository = userRepository;
        this.attendanceService = attendanceService;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<AttendanceResponseDTO> attendEvent(
            @PathVariable Long eventId,
            Principal principal) {

        // Obtener el usuario actual
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            // Registrar asistencia
            Attendance attendance = attendanceService.registerAttendance(eventId, currentUser.getId());

            // Crear DTO de respuesta
            AttendanceResponseDTO responseDTO = new AttendanceResponseDTO(
                    attendance.getId(),
                    currentUser.getId(),
                    currentUser.getUsername(),
                    eventId,
                    attendance.getTicketCode());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar asistencia: " + e.getMessage());
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> unattendEvent(
            @PathVariable Long eventId,
            Principal principal) {

        // Obtener el usuario actual
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Cancelar asistencia
        attendanceService.unregisterAttendance(eventId, currentUser.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<AttendanceResponseDTO>> getUserAttendances(Principal principal) {
        // Obtener el usuario actual
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener asistencias del usuario
        List<AttendanceResponseDTO> attendances = attendanceService.getUserAttendances(currentUser.getId());

        return ResponseEntity.ok(attendances);
    }
}