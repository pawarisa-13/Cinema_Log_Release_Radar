package com.cinemalog.service.notification;

import com.cinemalog.config.AppProperties;
import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.NotificationRepository;
import com.cinemalog.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender extends AbstractNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final AppProperties appProperties;
    private final String mailHost;

    public EmailNotificationSender(UserRepository userRepository, MovieRepository movieRepository,
                                   NotificationRepository notificationRepository,
                                   ObjectProvider<JavaMailSender> mailSender, AppProperties appProperties,
                                   @Value("${spring.mail.host:}") String mailHost) {
        super(userRepository, movieRepository, notificationRepository);
        this.mailSender = mailSender;
        this.appProperties = appProperties;
        this.mailHost = mailHost;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    protected void deliver(User user, NotificationMessage message) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || mailHost == null || mailHost.isBlank()) {
            log.info("Email not configured; would send to {}: {}", user.getEmail(), message.text());
            return;
        }
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(appProperties.mailFrom());
        mail.setTo(user.getEmail());
        mail.setSubject("Cinema Log · release reminder");
        mail.setText(message.text() + "\n\nOpen Cinema Log to see your release radar.");
        try {
            sender.send(mail);
        } catch (MailException ex) {
            log.warn("Could not email {}: {}", user.getEmail(), ex.getMessage());
        }
    }

    @Override
    protected String recordText(NotificationMessage message) {
        return message.text() + " (also sent to your email)";
    }
}
