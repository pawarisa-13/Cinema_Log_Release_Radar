package com.cinemalog.service.notification;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.Notification;
import com.cinemalog.domain.entity.User;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.NotificationRepository;
import com.cinemalog.repository.UserRepository;

public abstract class AbstractNotificationSender implements NotificationSender {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final NotificationRepository notificationRepository;

    protected AbstractNotificationSender(UserRepository userRepository, MovieRepository movieRepository,
                                         NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public final void send(NotificationMessage message) {
        User user = userRepository.findById(message.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User " + message.userId() + " was not found."));
        Movie movie = message.movieId() == null ? null : movieRepository.findById(message.movieId()).orElse(null);
        deliver(user, message);
        notificationRepository.save(new Notification(user, movie, message.type(), channel(), recordText(message)));
    }

    protected abstract void deliver(User user, NotificationMessage message);

    protected String recordText(NotificationMessage message) {
        return message.text();
    }
}
