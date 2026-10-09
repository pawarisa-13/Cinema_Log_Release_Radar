package com.cinemalog.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.NotificationType;
import com.cinemalog.domain.event.DiaryEntryLoggedEvent;
import com.cinemalog.domain.event.ReminderDueEvent;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class NotificationEventListenerTest {

    NotificationSenderFactory factory = mock(NotificationSenderFactory.class);
    NotificationSender sender = mock(NotificationSender.class);
    NotificationEventListener listener = new NotificationEventListener(factory);

    @Test
    void reminderDueUsesTheReminderChannel() {
        when(factory.forChannel(NotificationChannel.EMAIL)).thenReturn(sender);

        listener.onReminderDue(new ReminderDueEvent(1L, 2L, 3L, "Dune releases tomorrow.", NotificationChannel.EMAIL));

        ArgumentCaptor<NotificationMessage> msg = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(sender).send(msg.capture());
        assertThat(msg.getValue().type()).isEqualTo(NotificationType.RELEASE_REMINDER);
        assertThat(msg.getValue().userId()).isEqualTo(2L);
    }

    @Test
    void diaryEntryWithoutReviewSendsNothing() {
        listener.onDiaryEntryLogged(new DiaryEntryLoggedEvent(2L, 3L, "Dune", false));

        verify(factory, never()).forChannel(any());
    }
}
