package com.loopinback.loopinback.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequestDTO {
    private Long userId;
    private Long eventId;
    private String ticketCode;
}