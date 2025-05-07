package com.loopinback.loopinback.service.attendance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.exception.ResourceNotFoundException;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.model.Event;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.AttendanceRepository;
import com.loopinback.loopinback.repository.EventRepository;
import com.loopinback.loopinback.repository.UserRepository;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            UserRepository userRepository,
            EventRepository eventRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<AttendanceResponseDTO> getAllAttendances() {
        return attendanceRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public AttendanceResponseDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia no encontrada con id: " + id));
        return convertToResponseDTO(attendance);
    }

    /**
     * Registers a user for an event by creating an attendance entry.
     * 
     * @param eventId the ID of the event the user is registering for
     * @param userId  the ID of the user who is registering
     * @return the created Attendance object
     * @throws IllegalStateException     if the user is already registered for the
     *                                   event
     * @throws ResourceNotFoundException if either the user or event does not exist
     */

    @Override
    @Transactional
    public Attendance registerAttendance(Long eventId, Long userId) {

        if (Boolean.TRUE.equals(attendanceRepository.existsByUserIdAndEventId(userId, eventId))) {
            throw new IllegalStateException("El usuario ya está registrado para este evento");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Evento no encontrado con id: " + eventId));

        String ticketCode = UUID.randomUUID().toString();

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setEvent(event);
        attendance.setTicketCode(ticketCode);
        attendance.setRegisteredAt(LocalDateTime.now());

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void unregisterAttendance(Long eventId, Long userId) {
        attendanceRepository.findByUserIdAndEventId(userId, eventId)
                .ifPresentOrElse(
                        attendanceRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException(
                                    "Asistencia no encontrada para la usuario:" + userId + " y evento: " + eventId);
                        });
    }

    @Override
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asistencia no encontrada con id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    @Override
    public AttendanceResponseDTO verifyTicket(String ticketCode) {
        Attendance attendance = attendanceRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con el código: " + ticketCode));
        return convertToResponseDTO(attendance);
    }

    @Override
    public List<AttendanceResponseDTO> getEventAttendances(Long eventId) {
        return attendanceRepository.findByEventId(eventId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<AttendanceResponseDTO> getUserAttendances(Long userId) {
        return attendanceRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private AttendanceResponseDTO convertToResponseDTO(Attendance attendance) {
        return new AttendanceResponseDTO(
                attendance.getId(),
                attendance.getUser().getId(),
                attendance.getUser().getUsername(),
                attendance.getEvent().getId(),
                attendance.getEvent().getTitle());
    }
}