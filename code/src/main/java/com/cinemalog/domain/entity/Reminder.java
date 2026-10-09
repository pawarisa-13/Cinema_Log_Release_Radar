package com.cinemalog.domain.entity;

import java.time.Instant;
import java.time.LocalDate;

import com.cinemalog.domain.enums.NotificationChannel;
import com.cinemalog.domain.enums.ReminderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "offset_days", nullable = false)
    private Integer offsetDays;

    @Column(name = "reminder_date", nullable = false)
    private LocalDate reminderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private ReminderStatus status = ReminderStatus.SCHEDULED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    protected Reminder() {
    }

    public Reminder(User user, Movie movie, int offsetDays, NotificationChannel channel) {
        this.user = user;
        this.movie = movie;
        this.createdAt = Instant.now();
        reschedule(offsetDays, channel);
    }

    public void reschedule(int offsetDays, NotificationChannel channel) {
        this.offsetDays = offsetDays;
        this.channel = channel;
        this.reminderDate = movie.getReleaseDate().minusDays(offsetDays);
        this.status = status.reschedule();
        this.sentAt = null;
    }

    public void markSent(Instant when) {
        this.status = status.send();
        this.sentAt = when;
    }

    public void cancel() {
        this.status = status.cancel();
    }

    public boolean isDueOn(LocalDate day) {
        return status == ReminderStatus.SCHEDULED && !reminderDate.isAfter(day);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Movie getMovie() {
        return movie;
    }

    public Integer getOffsetDays() {
        return offsetDays;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public ReminderStatus getStatus() {
        return status;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
