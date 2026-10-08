package com.cinemalog.service.notification;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.cinemalog.domain.enums.NotificationChannel;

import org.springframework.stereotype.Component;

@Component
public class NotificationSenderFactory {

    private final Map<NotificationChannel, NotificationSender> senders = new EnumMap<>(NotificationChannel.class);

    public NotificationSenderFactory(List<NotificationSender> allSenders) {
        allSenders.forEach(s -> senders.put(s.channel(), s));
    }

    public NotificationSender forChannel(NotificationChannel channel) {
        NotificationSender sender = senders.get(channel);
        if (sender == null) {
            throw new IllegalStateException("No notification sender registered for " + channel);
        }
        return sender;
    }
}
