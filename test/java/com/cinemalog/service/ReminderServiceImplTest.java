package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.Reminder;
import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.ReminderStatus;
import com.cinemalog.domain.model.ExternalMovie;
import com.cinemalog.dto.request.ReminderRequest;
import com.cinemalog.exception.BusinessRuleException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.ReminderMapper;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.ReminderRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.impl.ReminderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ReminderServiceImplTest {

    static final LocalDate TODAY = LocalDate.of(2026, 10, 3);

    ReminderRepository reminderRepository = mock(ReminderRepository.class);
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    ReminderDispatchService dispatchService = mock(ReminderDispatchService.class);
    ReminderMapper mapper = mock(ReminderMapper.class);
    ReminderServiceImpl service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneId.of("UTC"));
        service = new ReminderServiceImpl(reminderRepository, movieRepository, userRepository, dispatchService, mapper, clock);
    }

    static Movie movieOutOn(LocalDate date) {
        Movie m = new Movie(99L, "Street Fighter");
        m.applySummary(new ExternalMovie(99L, "Street Fighter", null, null, null, null, date, 0.0, 1.0, "en",
                List.of(), null, null, null), List.of());
        return m;
    }

    @Test
    void onlyOffsetsOf7_3_1_0AreAllowed() {
        assertThatThrownBy(() -> service.save(1L, 5L, new ReminderRequest(2, NotificationChannel.IN_APP)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cannotRemindAboutAMovieThatIsAlreadyOut() {
        when(movieRepository.findById(5L)).thenReturn(Optional.of(movieOutOn(TODAY)));

        assertThatThrownBy(() -> service.save(1L, 5L, new ReminderRequest(1, NotificationChannel.IN_APP)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already out");
    }

    @Test
    void createsAReminderDatedOffsetDaysBeforeRelease() {
        Movie movie = movieOutOn(TODAY.plusDays(13));
        when(movieRepository.findById(5L)).thenReturn(Optional.of(movie));
        when(reminderRepository.findByUserIdAndMovieId(1L, 5L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(1L)).thenReturn(mock(User.class));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(inv -> inv.getArgument(0));

        service.save(1L, 5L, new ReminderRequest(3, NotificationChannel.EMAIL));

        ArgumentCaptor<Reminder> saved = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(saved.capture());
        assertThat(saved.getValue().getReminderDate()).isEqualTo(TODAY.plusDays(10));
        assertThat(saved.getValue().getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(saved.getValue().getStatus()).isEqualTo(ReminderStatus.SCHEDULED);
        verify(dispatchService).dispatchIfDue(saved.getValue());
    }

    @Test
    void savingAgainReschedulesTheExistingReminder() {
        Movie movie = movieOutOn(TODAY.plusDays(13));
        Reminder existing = new Reminder(mock(User.class), movie, 7, NotificationChannel.IN_APP);
        when(movieRepository.findById(5L)).thenReturn(Optional.of(movie));
        when(reminderRepository.findByUserIdAndMovieId(1L, 5L)).thenReturn(Optional.of(existing));

        service.save(1L, 5L, new ReminderRequest(1, NotificationChannel.IN_APP));

        assertThat(existing.getOffsetDays()).isEqualTo(1);
        assertThat(existing.getReminderDate()).isEqualTo(TODAY.plusDays(12));
        verify(reminderRepository, never()).save(any());
    }

    @Test
    void cancelUnknownReminderIs404() {
        when(reminderRepository.findByUserIdAndMovieId(1L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(1L, 5L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
