package com.loopinback.loopinback.service.event;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
import com.loopinback.loopinback.dto.event.EventRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.model.Category;
import com.loopinback.loopinback.model.Event;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.AttendanceRepository;
import com.loopinback.loopinback.repository.EventRepository;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AttendanceRepository attendanceRepository;

    public EventServiceImpl(EventRepository eventRepository, AttendanceRepository attendanceRepository) {
        this.eventRepository = eventRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public EventResponseDTO updateEvent(EventRequestDTO dto) {
        Event event = eventRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        populateEventFromDto(event, dto);
        Event updatedEvent = eventRepository.save(event);
        return convertToDto(updatedEvent);
    }

    @Override
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<EventResponseDTO> getAllEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        return convertToDto(event);
    }

    @Override
    public EventResponseDTO createEvent(EventRequestDTO dto, User creator) {
        Event event = new Event();
        populateEventFromDto(event, dto);
        event.setCreator(creator);
        Event savedEvent = eventRepository.save(event);
        return convertToDto(savedEvent);
    }

    @Override
    public EventResponseDTO updateEvent(EventRequestDTO dto, User user) {
        Event event = eventRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if (!event.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para editar este evento");
        }

        populateEventFromDto(event, dto);
        Event updated = eventRepository.save(event);
        return convertToDto(updated);
    }

    @Override
    public void deleteEvent(Long id, User user) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        if (!event.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este evento");
        }
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventResponseDTO> getEventsByCategory(String categoryStr, int page, int size) {
        Category category = Category.valueOf(categoryStr.toUpperCase());
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findByCategory(category, pageable)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<EventResponseDTO> findEventsByFilters(String category,
            String username, String eventName, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findByFilters(
                category != null ? Category.valueOf(category.toUpperCase()) : null,
                eventName, date != null ? date.atStartOfDay() : null,
                username, pageable).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AttendanceResponseDTO> getEventAttendances(Long eventId) {
        return attendanceRepository.findByEventId(eventId).stream()
                .map(this::convertToAttendanceDto)
                .toList();
    }

    @Override
    public Page<EventResponseDTO> getUserEvents(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findByCreator(user, pageable)
                .map(this::convertToDto);
    }

    @Override
    public AttendanceResponseDTO attendEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if (Boolean.TRUE.equals(attendanceRepository.existsByUserIdAndEventId(user.getId(), eventId))) {
            throw new RuntimeException("Ya estás registrado en este evento");
        }

        if (event.getMaxAttendees() != null) {
            long currentAttendees = attendanceRepository.countByEventId(eventId);
            if (currentAttendees >= event.getMaxAttendees()) {
                throw new RuntimeException("El evento ha alcanzado su capacidad máxima");
            }
        }

        Attendance attendance = new Attendance();
        attendance.setEvent(event);
        attendance.setUser(user);
        attendance.setTicketCode(generateTicketCode(user, event));

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return convertToAttendanceDto(savedAttendance);
    }

    @Override
    public void unattendEvent(Long eventId, User user) {
        attendanceRepository.deleteByEventIdAndUserId(eventId, user.getId());
    }

    private String generateTicketCode(User user, Event event) {
        return "TICKET-" + user.getId() + "-" + event.getId() + "-" + System.currentTimeMillis();
    }

    private EventResponseDTO convertToDto(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getCategory(),
                event.getCreator().getUsername(),
                event.getImageUrl(),
                event.getMaxAttendees());
    }

    private AttendanceResponseDTO convertToAttendanceDto(Attendance attendance) {
        return new AttendanceResponseDTO(
                attendance.getId(),
                attendance.getUser().getId(),
                attendance.getUser().getUsername(),
                attendance.getEvent().getId(),
                attendance.getTicketCode());
    }

    private void populateEventFromDto(Event event, EventRequestDTO dto) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setLocation(dto.getLocation());
        event.setCategory(dto.getCategory());
        event.setMaxAttendees(dto.getMaxAttendees());

        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            event.setImageUrl(dto.getImageUrl());
        }
    }
}

/*
 * package com.loopinback.loopinback.service.event;
 * 
 * import java.time.LocalDate;
 * import java.util.List;
 * 
 * import org.springframework.data.domain.Page;
 * import org.springframework.data.domain.PageRequest;
 * import org.springframework.data.domain.Pageable;
 * import org.springframework.stereotype.Service;
 * import org.springframework.transaction.annotation.Transactional;
 * 
 * import com.loopinback.loopinback.dto.attendance.AttendanceResponseDTO;
 * import com.loopinback.loopinback.dto.event.EventRequestDTO;
 * import com.loopinback.loopinback.dto.event.EventResponseDTO;
 * import com.loopinback.loopinback.model.Attendance;
 * import com.loopinback.loopinback.model.Category;
 * import com.loopinback.loopinback.model.Event;
 * import com.loopinback.loopinback.model.User;
 * import com.loopinback.loopinback.repository.AttendanceRepository;
 * import com.loopinback.loopinback.repository.EventRepository;
 * 
 * @Service
 * 
 * @Transactional
 * public class EventServiceImpl implements EventService {
 * 
 * @Override
 * public void deleteEvent(Long id) {
 * throw new UnsupportedOperationException("Not implemented yet");
 * }
 * 
 * @Override
 * public EventResponseDTO updateEvent(EventRequestDTO dto) {
 * throw new UnsupportedOperationException("Not implemented yet");
 * }
 * 
 * private final EventRepository eventRepository;
 * private final AttendanceRepository attendanceRepository;
 * 
 * public EventServiceImpl(EventRepository eventRepository,
 * AttendanceRepository attendanceRepository) {
 * this.eventRepository = eventRepository;
 * this.attendanceRepository = attendanceRepository;
 * }
 * 
 * @Override
 * public List<EventResponseDTO> getAllEvents() {
 * return eventRepository.findAll().stream()
 * .map(this::convertToDto)
 * .toList();
 * }
 * 
 * @Override
 * public Page<EventResponseDTO> getAllEvents(int page, int size) {
 * Pageable pageable = PageRequest.of(page, size);
 * return eventRepository.findAll(pageable)
 * .map(this::convertToDto);
 * }
 * 
 * @Override
 * public EventResponseDTO getEventById(Long id) {
 * Event event = eventRepository.findById(id)
 * .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
 * return convertToDto(event);
 * }
 * 
 * @Override
 * public EventResponseDTO createEvent(EventRequestDTO dto, User creator) {
 * Event event = new Event();
 * populateEventFromDto(event, dto);
 * event.setCreator(creator);
 * Event savedEvent = eventRepository.save(event);
 * return convertToDto(savedEvent);
 * }
 * 
 * @Override
 * public EventResponseDTO updateEvent(EventRequestDTO dto, User user) {
 * Event event = eventRepository.findById(dto.getId())
 * .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
 * if (!event.getCreator().getId().equals(user.getId())) {
 * throw new RuntimeException("No tienes permiso para editar este evento");
 * }
 * populateEventFromDto(event, dto);
 * Event updated = eventRepository.save(event);
 * return convertToDto(updated);
 * }
 * 
 * @Override
 * public void deleteEvent(Long id, User user) {
 * Event event = eventRepository.findById(id)
 * .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
 * if (!event.getCreator().getId().equals(user.getId())) {
 * throw new RuntimeException("No tienes permiso para eliminar este evento");
 * }
 * eventRepository.deleteById(id);
 * }
 * 
 * @Override
 * public List<EventResponseDTO> getEventsByCategory(String categoryStr, int
 * page, int size) {
 * Category category = Category.valueOf(categoryStr.toUpperCase());
 * Pageable pageable = PageRequest.of(page, size);
 * return eventRepository.findByCategory(category, pageable)
 * .stream()
 * .map(this::convertToDto)
 * .toList();
 * }
 * 
 * @Override
 * public List<EventResponseDTO> findEventsByFilters(
 * String category,
 * String username,
 * String eventName,
 * LocalDate date,
 * int page,
 * int size) {
 * Pageable pageable = PageRequest.of(page, size);
 * return eventRepository.findByFilters(
 * category != null ? Category.valueOf(category.toUpperCase()) : null,
 * eventName,
 * date != null ? date.atStartOfDay() : null,
 * username,
 * pageable).stream()
 * .map(this::convertToDto)
 * .toList();
 * }
 * 
 * @Override
 * public List<AttendanceResponseDTO> getEventAttendances(Long eventId) {
 * return attendanceRepository.findByEventId(eventId).stream()
 * .map(this::convertToAttendanceDto)
 * .toList();
 * }
 * 
 * @Override
 * public Page<EventResponseDTO> getUserEvents(User user, int page, int size) {
 * Pageable pageable = PageRequest.of(page, size);
 * return eventRepository.findByCreator(user, pageable)
 * .map(this::convertToDto);
 * }
 * 
 * @Override
 * public AttendanceResponseDTO attendEvent(Long eventId, User user) {
 * // Buscar el evento
 * Event event = eventRepository.findById(eventId)
 * .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
 * 
 * // Verificar si el usuario ya está registrado
 * if
 * (Boolean.TRUE.equals(attendanceRepository.existsByUserIdAndEventId(user.getId
 * (), eventId))) {
 * throw new RuntimeException("Ya estás registrado en este evento");
 * }
 * 
 * // Verificar si hay cupo disponible
 * if (event.getMaxAttendees() != null) {
 * long currentAttendees = attendanceRepository.countByEventId(eventId);
 * if (currentAttendees >= event.getMaxAttendees()) {
 * throw new RuntimeException("El evento ha alcanzado su capacidad máxima");
 * }
 * }
 * 
 * // Crear la asistencia
 * Attendance attendance = new Attendance();
 * attendance.setEvent(event);
 * attendance.setUser(user);
 * attendance.setTicketCode(generateTicketCode(user, event));
 * 
 * Attendance savedAttendance = attendanceRepository.save(attendance);
 * return convertToAttendanceDto(savedAttendance);
 * }
 * 
 * @Override
 * public void unattendEvent(Long eventId, User user) {
 * attendanceRepository.deleteByEventIdAndUserId(eventId, user.getId());
 * }
 * 
 * private String generateTicketCode(User user, Event event) {
 * return "TICKET-" + user.getId() + "-" + event.getId() + "-" +
 * System.currentTimeMillis();
 * }
 * 
 * private EventResponseDTO convertToDto(Event event) {
 * return new EventResponseDTO(
 * event.getId(),
 * event.getTitle(),
 * event.getDescription(),
 * event.getStartTime(),
 * event.getEndTime(),
 * event.getLocation(),
 * event.getCategory(),
 * event.getCreator().getUsername(),
 * event.getImageUrl(),
 * event.getMaxAttendees());
 * }
 * 
 * private AttendanceResponseDTO convertToAttendanceDto(Attendance attendance) {
 * return new AttendanceResponseDTO(
 * attendance.getId(),
 * attendance.getUser().getId(),
 * attendance.getUser().getUsername(),
 * attendance.getEvent().getId(),
 * attendance.getTicketCode());
 * }
 * 
 * private void populateEventFromDto(Event event, EventRequestDTO dto) {
 * event.setTitle(dto.getTitle());
 * event.setDescription(dto.getDescription());
 * event.setStartTime(dto.getStartTime());
 * event.setEndTime(dto.getEndTime());
 * event.setLocation(dto.getLocation());
 * event.setCategory(dto.getCategory());
 * event.setMaxAttendees(dto.getMaxAttendees());
 * 
 * // Solo actualizar la imagen si se proporciona una nueva
 * if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
 * event.setImageUrl(dto.getImageUrl());
 * }
 * }
 * }
 * 
 */