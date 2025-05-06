package com.loopinback.loopinback.service.event;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.dto.event.EventRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.model.User;

public interface EventService {
    // Método sin paginación (mantener para compatibilidad)
    List<EventResponseDTO> getAllEvents();

    // Método con paginación usado en el controlador
    Page<EventResponseDTO> getAllEvents(int page, int size);

    // Método para obtener un evento por ID
    EventResponseDTO getEventById(Long id);

    // Método para crear un evento
    EventResponseDTO createEvent(EventRequestDTO eventDTO, User creator);

    // Método para actualizar un evento (con verificación de usuario)
    EventResponseDTO updateEvent(EventRequestDTO eventDTO, User user);

    // Método para actualizar un evento (sin verificación de usuario, para backwards
    // compatibility)
    EventResponseDTO updateEvent(EventRequestDTO eventDTO);

    // Método para eliminar un evento
    void deleteEvent(Long id);

    // Método para eliminar un evento (con verificación de usuario)
    void deleteEvent(Long id, User user);

    // Método para obtener asistentes a un evento
    List<AttendanceResponseDTO> getEventAttendances(Long eventId);

    // Método para obtener eventos por categoría
    List<EventResponseDTO> getEventsByCategory(String category, int page, int size);

    // Método para buscar eventos por filtros
    List<EventResponseDTO> findEventsByFilters(
            String category,
            String username,
            String eventName,
            LocalDate date,
            int page,
            int size);

    // Método para obtener eventos de un usuario
    Page<EventResponseDTO> getUserEvents(User user, int page, int size);

    // Método para apuntarse a un evento
    AttendanceResponseDTO attendEvent(Long eventId, User user);

    // Método para desapuntarse de un evento
    void unattendEvent(Long eventId, User user);
}
