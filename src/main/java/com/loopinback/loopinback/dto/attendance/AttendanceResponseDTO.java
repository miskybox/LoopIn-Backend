package com.loopinback.loopinback.dto.attendance;

import java.time.LocalDateTime;

import com.loopinback.loopinback.model.AttendanceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {
    private Long id;
    private Long eventId;
    private Long userId;
    private String username;
    private AttendanceStatus status;
    private LocalDateTime createdAt;
    private String ticketCode;

    public AttendanceResponseDTO(Long id, Long userId, String username, Long eventId, String ticketCode) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.eventId = eventId;
        this.ticketCode = ticketCode;
        this.status = AttendanceStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }
}
