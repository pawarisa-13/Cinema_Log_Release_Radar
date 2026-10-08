package com.cinemalog.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.Reminder;
import com.cinemalog.domain.enums.ReminderStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Optional<Reminder> findByUserIdAndMovieId(Long userId, Long movieId);

    @EntityGraph(attributePaths = "movie")
    List<Reminder> findByUserIdAndStatusInOrderByReminderDateAsc(Long userId, Collection<ReminderStatus> statuses);

    @EntityGraph(attributePaths = {"movie", "user"})
    List<Reminder> findByStatusAndReminderDateLessThanEqual(ReminderStatus status, LocalDate day);
}
