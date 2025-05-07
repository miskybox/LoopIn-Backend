package com.loopinback.loopinback.service.attendance;

import java.util.List;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.model.Attendance;

public interface AttendanceService {

    List<AttendanceResponseDTO> getAllAttendances();

    AttendanceResponseDTO getAttendanceById(Long id);

    Attendance registerAttendance(Long eventId, Long userId);

    void unregisterAttendance(Long eventId, Long userId);

    void deleteAttendance(Long id);

    AttendanceResponseDTO verifyTicket(String ticketCode);

    List<AttendanceResponseDTO> getEventAttendances(Long eventId);

    List<AttendanceResponseDTO> getUserAttendances(Long userId);
}
