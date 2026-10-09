package com.cinemalog.service.impl;

import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.Reminder;
import com.cinemalog.domain.enums.ReminderStatus;
import com.cinemalog.dto.request.ReminderRequest;
import com.cinemalog.dto.response.ReminderResponse;
import com.cinemalog.exception.BusinessRuleException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.ReminderMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.ReminderRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.ReminderDispatchService;
import com.cinemalog.service.ReminderService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReminderServiceImpl implements ReminderService {

    static final Set<Integer> ALLOWED_OFFSETS = Set.of(0, 1, 3, 7);

    private final ReminderRepository reminderRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReminderDispatchService dispatchService;
    private final ReminderMapper reminderMapper;
    private final Clock clock;

    public ReminderServiceImpl(ReminderRepository reminderRepository, MovieRepository movieRepository,
                               UserRepository userRepository, ReminderDispatchService dispatchService,
                               ReminderMapper reminderMapper, Clock clock) {
        this.reminderRepository = reminderRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.dispatchService = dispatchService;
        this.reminderMapper = reminderMapper;
        this.clock = clock;
    }

    @Override
    public List<ReminderResponse> listActive(Long userId) {
        LocalDate today = LocalDate.now(clock);
        return reminderRepository
                .findByUserIdAndStatusInOrderByReminderDateAsc(userId, EnumSet.of(ReminderStatus.SCHEDULED, ReminderStatus.SENT))
                .stream()
                .filter(r -> !r.getMovie().getReleaseDate().isBefore(today))
                .map(reminderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReminderResponse save(Long userId, Long movieId, ReminderRequest request) {
        if (!ALLOWED_OFFSETS.contains(request.offsetDays())) {
            throw new BusinessRuleException("Pick 7, 3, 1 or 0 days before release.");
        }
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + movieId + " was not found."));
        LocalDate today = LocalDate.now(clock);
        if (movie.getReleaseDate() == null || !movie.getReleaseDate().isAfter(today)) {
            throw new BusinessRuleException(movie.getTitle() + " is already out, so there is nothing to remind you about.");
        }
        Reminder reminder = reminderRepository.findByUserIdAndMovieId(userId, movieId)
                .map(existing -> {
                    existing.reschedule(request.offsetDays(), request.channel());
                    return existing;
                })
                .orElseGet(() -> reminderRepository.save(
                        new Reminder(userRepository.getReferenceById(userId), movie, request.offsetDays(), request.channel())));
        dispatchService.dispatchIfDue(reminder);
        return reminderMapper.toResponse(reminder);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long movieId) {
        Reminder reminder = reminderRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new ResourceNotFoundException("You have no reminder for this movie."));
        reminder.cancel();
    }
}
