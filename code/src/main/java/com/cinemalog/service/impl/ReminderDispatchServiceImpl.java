package com.cinemalog.service.impl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.cinemalog.domain.entity.Reminder;
import com.cinemalog.domain.enums.ReminderStatus;
import com.cinemalog.domain.event.ReminderDueEvent;
import com.cinemalog.repository.ReminderRepository;
import com.cinemalog.service.ReminderDispatchService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReminderDispatchServiceImpl implements ReminderDispatchService {

    private final ReminderRepository reminderRepository;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;

    public ReminderDispatchServiceImpl(ReminderRepository reminderRepository, ApplicationEventPublisher publisher, Clock clock) {
        this.reminderRepository = reminderRepository;
        this.publisher = publisher;
        this.clock = clock;
    }

    @Override
    @Transactional
    public int dispatchDueReminders() {
        LocalDate today = LocalDate.now(clock);
        List<Reminder> due = reminderRepository.findByStatusAndReminderDateLessThanEqual(ReminderStatus.SCHEDULED, today);
        due.forEach(r -> send(r, today));
        return due.size();
    }

    @Override
    @Transactional
    public boolean dispatchIfDue(Reminder reminder) {
        LocalDate today = LocalDate.now(clock);
        if (!reminder.isDueOn(today)) {
            return false;
        }
        send(reminder, today);
        return true;
    }

    private void send(Reminder reminder, LocalDate today) {
        long daysLeft = ChronoUnit.DAYS.between(today, reminder.getMovie().getReleaseDate());
        publisher.publishEvent(new ReminderDueEvent(reminder.getId(), reminder.getUser().getId(),
                reminder.getMovie().getId(), message(reminder.getMovie().getTitle(), daysLeft), reminder.getChannel()));
        reminder.markSent(Instant.now(clock));
    }

    static String message(String title, long daysLeft) {
        if (daysLeft <= 0) {
            return title + " is out today.";
        }
        if (daysLeft == 1) {
            return title + " releases tomorrow.";
        }
        return title + " releases in " + daysLeft + " days.";
    }
}
