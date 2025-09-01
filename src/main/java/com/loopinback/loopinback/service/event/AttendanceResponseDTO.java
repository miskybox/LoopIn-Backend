package com.loopinback.loopinback.service.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponseDTO {
    private Long id;
    private Long eventId;
    private Long userId;
    private String username;
    private String eventTitle;
    private String ticketCode;
    private LocalDateTime registeredAt;
}