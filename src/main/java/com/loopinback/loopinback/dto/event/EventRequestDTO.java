package com.loopinback.loopinback.dto.event;

import java.time.LocalDateTime;

import com.loopinback.loopinback.model.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Category category;
    private Integer maxAttendees;
    private String location;
}