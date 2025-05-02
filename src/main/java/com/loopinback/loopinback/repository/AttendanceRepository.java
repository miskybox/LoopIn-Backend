package com.loopinback.loopinback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loopinback.loopinback.model.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByUserId(Long userId);

    List<Attendance> findByEventId(Long eventId);

    Optional<Attendance> findByUserIdAndEventId(Long userId, Long eventId);

    Boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Integer countByEventId(Long eventId);

    void deleteByEventIdAndUserId(Long eventId, Long userId);

    Optional<Attendance> findByTicketCode(String ticketCode);
}
