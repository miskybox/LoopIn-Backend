package com.loopinback.loopinback.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.loopinback.loopinback.model.Category;
import com.loopinback.loopinback.model.Event;
import com.loopinback.loopinback.model.User;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByCategory(Category category, Pageable pageable);

    Page<Event> findByCreator(User creator, Pageable pageable);

    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE DATE(e.startTime) = DATE(:date)")
    Page<Event> findByDate(@Param("date") LocalDateTime date, Pageable pageable);

    List<Event> findByCreatorId(Long creatorId);

    @Query("SELECT e FROM Event e WHERE " +
            "(:category IS NULL OR e.category = :category) AND " +
            "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:startDate IS NULL OR DATE(e.startTime) = DATE(:startDate)) AND " +
            "(:creatorUsername IS NULL OR e.creator.username = :creatorUsername)")
    Page<Event> findByFilters(
            @Param("category") Category category,
            @Param("title") String title,
            @Param("startDate") LocalDateTime startDate,
            @Param("creatorUsername") String creatorUsername,
            Pageable pageable);
}