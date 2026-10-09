package com.cinemalog.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.cinemalog.domain.enums.NotificationChannel;

import org.junit.jupiter.api.Test;

class NotificationSenderFactoryTest {

    @Test
    void returnsTheSenderRegisteredForTheChannel() {
        NotificationSender inApp = mock(NotificationSender.class);
        NotificationSender email = mock(NotificationSender.class);
        when(inApp.channel()).thenReturn(NotificationChannel.IN_APP);
        when(email.channel()).thenReturn(NotificationChannel.EMAIL);

        NotificationSenderFactory factory = new NotificationSenderFactory(List.of(inApp, email));

        assertThat(factory.forChannel(NotificationChannel.IN_APP)).isSameAs(inApp);
        assertThat(factory.forChannel(NotificationChannel.EMAIL)).isSameAs(email);
    }

    @Test
    void failsLoudlyWhenAChannelHasNoSender() {
        NotificationSender inApp = mock(NotificationSender.class);
        when(inApp.channel()).thenReturn(NotificationChannel.IN_APP);
        NotificationSenderFactory factory = new NotificationSenderFactory(List.of(inApp));

        assertThatThrownBy(() -> factory.forChannel(NotificationChannel.EMAIL)).isInstanceOf(IllegalStateException.class);
    }
}
