/* 
package com.loopinback.loopinback.service.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.loopinback.loopinback.dto.attendance.AttendanceRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.dto.user.UserRequestDTO;
import com.loopinback.loopinback.dto.user.UserResponseDTO;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.model.Event;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public List<EventResponseDTO> getEventsCreatedByUser(Long userId) {
        User user = getUserEntity(userId);
        return user.getCreatedEvents().stream()
                .map(this::convertToEventResponseDTO)
                .toList();
    }

    @Override
    public List<AttendanceRequestDTO> getAttendancesByUser(Long userId) {
        User user = getUserEntity(userId);
        return user.getAttendances().stream()
                .map(this::convertToAttendanceRequestDTO)
                .toList();
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return convertToResponseDTO(getUserEntity(id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userDTO) {
        User user = convertToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (Boolean.TRUE.equals(userRepository.existsByUsername(user.getUsername()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está en uso");
        }
        User saved = userRepository.save(user);
        return convertToResponseDTO(saved);
    }

    @Override
    public UserResponseDTO updateUser(UserRequestDTO userDTO) {
        User user = getUserEntity(userDTO.getId());
        updateEntityFromDto(userDTO, user);
        User updated = userRepository.save(user);
        return convertToResponseDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageUrl());
    }

    private User convertToEntity(UserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .profileImageUrl(dto.getProfileImageUrl())
                .build();
    }

    private void updateEntityFromDto(UserRequestDTO dto, User user) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setProfileImageUrl(dto.getProfileImageUrl());
    }

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private EventResponseDTO convertToEventResponseDTO(Event event) {
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

    private AttendanceRequestDTO convertToAttendanceRequestDTO(Attendance attendance) {
        AttendanceRequestDTO dto = new AttendanceRequestDTO();
        dto.setUserId(attendance.getUser().getId());
        dto.setEventId(attendance.getEvent().getId());
        dto.setTicketCode(attendance.getTicketCode());
        return dto;
    }
}
    */
package com.loopinback.loopinback.service.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.loopinback.loopinback.dto.attendance.AttendanceRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.dto.user.UserRequestDTO;
import com.loopinback.loopinback.dto.user.UserResponseDTO;
import com.loopinback.loopinback.model.Attendance;
import com.loopinback.loopinback.model.Event;
import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // O inyectable vía configuración si deseas más flexibilidad
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return convertToResponseDTO(getUserEntity(id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userDTO) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userDTO.getUsername()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está en uso");
        }

        User user = convertToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return convertToResponseDTO(saved);
    }

    @Override
    public UserResponseDTO updateUser(UserRequestDTO userDTO) {
        User user = getUserEntity(userDTO.getId());
        updateEntityFromDto(userDTO, user);
        User updated = userRepository.save(user);
        return convertToResponseDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<EventResponseDTO> getEventsCreatedByUser(Long userId) {
        User user = getUserEntity(userId);
        return user.getCreatedEvents().stream()
                .map(this::convertToEventResponseDTO)
                .toList();
    }

    @Override
    public List<AttendanceRequestDTO> getAttendancesByUser(Long userId) {
        User user = getUserEntity(userId);
        return user.getAttendances().stream()
                .map(this::convertToAttendanceRequestDTO)
                .toList();
    }

    @Override
    public UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageUrl());
    }

    // Helpers

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private User convertToEntity(UserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .profileImageUrl(dto.getProfileImageUrl())
                .build();
    }

    private void updateEntityFromDto(UserRequestDTO dto, User user) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setProfileImageUrl(dto.getProfileImageUrl());
    }

    private EventResponseDTO convertToEventResponseDTO(Event event) {
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

    private AttendanceRequestDTO convertToAttendanceRequestDTO(Attendance attendance) {
        AttendanceRequestDTO dto = new AttendanceRequestDTO();
        dto.setUserId(attendance.getUser().getId());
        dto.setEventId(attendance.getEvent().getId());
        dto.setTicketCode(attendance.getTicketCode());
        return dto;
    }
}
