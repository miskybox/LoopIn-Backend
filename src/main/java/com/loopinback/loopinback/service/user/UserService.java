package com.loopinback.loopinback.service.user;
import java.util.List;

import com.loopinback.loopinback.dto.attendance.AttendanceRequestDTO;
import com.loopinback.loopinback.dto.event.EventResponseDTO;
import com.loopinback.loopinback.dto.user.UserRequestDTO;
import com.loopinback.loopinback.dto.user.UserResponseDTO;
import com.loopinback.loopinback.model.User;

public interface UserService {
    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO registerUser(UserRequestDTO userDTO);

    UserResponseDTO updateUser(UserRequestDTO userDTO);

    void deleteUser(Long id);

    List<EventResponseDTO> getEventsCreatedByUser(Long userId);

    List<AttendanceRequestDTO> getAttendancesByUser(Long userId);


    UserResponseDTO convertToResponseDTO(User user);
}
