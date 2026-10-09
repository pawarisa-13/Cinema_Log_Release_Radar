package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.Reminder;
import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.ReminderStatus;
import com.cinemalog.domain.event.ReminderDueEvent;
import com.cinemalog.domain.model.ExternalMovie;
import com.cinemalog.repository.ReminderRepository;
import com.cinemalog.service.impl.ReminderDispatchServiceImpl;
import com.cinemalog.service.impl.ReminderDispatchServiceImplTestAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

class ReminderDispatchServiceImplTest {

    static final LocalDate TODAY = LocalDate.of(2026, 10, 3);

    ReminderRepository repository = mock(ReminderRepository.class);
    ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    ReminderDispatchServiceImpl service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneId.of("UTC"));
        service = new ReminderDispatchServiceImpl(repository, publisher, clock);
    }

    static Reminder reminderFor(LocalDate release, int offset) {
        Movie m = new Movie(1L, "The Social Reckoning");
        m.applySummary(new ExternalMovie(1L, "The Social Reckoning", null, null, null, null, release, 0.0, 1.0, "en",
                List.of(), null, null, null), List.of());
        return new Reminder(mock(User.class), m, offset, NotificationChannel.IN_APP);
    }

    @Test
    void sendsEveryDueReminder() {
        Reminder due = reminderFor(TODAY.plusDays(6), 7);
        when(repository.findByStatusAndReminderDateLessThanEqual(ReminderStatus.SCHEDULED, TODAY)).thenReturn(List.of(due));

        int sent = service.dispatchDueReminders();

        assertThat(sent).isEqualTo(1);
        assertThat(due.getStatus()).isEqualTo(ReminderStatus.SENT);
        ArgumentCaptor<ReminderDueEvent> event = ArgumentCaptor.forClass(ReminderDueEvent.class);
        verify(publisher).publishEvent(event.capture());
        assertThat(event.getValue().message()).isEqualTo("The Social Reckoning releases in 6 days.");
    }

    @Test
    void doesNothingWhenTheDateIsStillAhead() {
        Reminder notYet = reminderFor(TODAY.plusDays(10), 1);

        assertThat(service.dispatchIfDue(notYet)).isFalse();
        assertThat(notYet.getStatus()).isEqualTo(ReminderStatus.SCHEDULED);
        verify(publisher, never()).publishEvent(org.mockito.ArgumentMatchers.any(Object.class));
    }

    @Test
    void wordsTheMessageByDaysLeft() {
        assertThat(ReminderDispatchServiceImplTestAccess.message("Dune", 0)).isEqualTo("Dune is out today.");
        assertThat(ReminderDispatchServiceImplTestAccess.message("Dune", 1)).isEqualTo("Dune releases tomorrow.");
        assertThat(ReminderDispatchServiceImplTestAccess.message("Dune", 3)).isEqualTo("Dune releases in 3 days.");
    }
}
