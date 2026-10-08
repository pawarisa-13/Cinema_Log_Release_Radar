package com.cinemalog.service.notification;

import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.NotificationRepository;
import com.cinemalog.repository.UserRepository;

import org.springframework.stereotype.Component;

@Component
public class InAppNotificationSender extends AbstractNotificationSender {

    public InAppNotificationSender(UserRepository userRepository, MovieRepository movieRepository,
                                   NotificationRepository notificationRepository) {
        super(userRepository, movieRepository, notificationRepository);
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    protected void deliver(User user, NotificationMessage message) {

    }
}
