package com.loopinback.loopinback.dto.event;

import java.time.LocalDateTime;

import com.loopinback.loopinback.model.Category;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String title;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String description;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    @Future(message = "La fecha y hora de inicio debe ser futura")
    private LocalDateTime startTime;

    @NotNull(message = "La fecha y hora de finalización es obligatoria")
    @Future(message = "La fecha y hora de finalización debe ser futura")
    private LocalDateTime endTime;

    @NotNull(message = "La categoría es obligatoria")
    private Category category;

    @NotNull(message = "El número máximo de asistentes es obligatorio")
    @Min(value = 1, message = "El número máximo de asistentes debe ser al menos 1")
    private Integer maxAttendees;

    @NotBlank(message = "La ubicación no puede estar vacía")
    private String location;

    private String imageUrl;
}
