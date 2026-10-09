package com.cinemalog.service.notification;

import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.NotificationType;
import com.cinemalog.domain.event.DiaryEntryLoggedEvent;
import com.cinemalog.domain.event.ReminderDueEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationEventListener {

    private final NotificationSenderFactory senderFactory;

    public NotificationEventListener(NotificationSenderFactory senderFactory) {
        this.senderFactory = senderFactory;
    }

    @EventListener
    @Transactional
    public void onReminderDue(ReminderDueEvent event) {
        senderFactory.forChannel(event.channel()).send(
                new NotificationMessage(event.userId(), event.movieId(), NotificationType.RELEASE_REMINDER, event.message()));
    }

    @EventListener
    @Transactional
    public void onDiaryEntryLogged(DiaryEntryLoggedEvent event) {
        if (!event.reviewAdded()) {
            return;
        }
        senderFactory.forChannel(NotificationChannel.IN_APP).send(new NotificationMessage(event.userId(), event.movieId(),
                NotificationType.REVIEW_ADDED, "Your review of " + event.movieTitle() + " was added successfully."));
    }
}
