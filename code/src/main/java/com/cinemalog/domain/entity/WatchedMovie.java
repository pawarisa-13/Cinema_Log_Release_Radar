package com.cinemalog.domain.entity;

import java.time.Instant;
import java.time.LocalDate;

import com.cinemalog.domain.enums.WatchPlace;

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
@Table(name = "watched_movies")
public class WatchedMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "watched_date", nullable = false)
    private LocalDate watchedDate;

    private Integer rating;

    @Column(length = 1000)
    private String review;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private WatchPlace place = WatchPlace.HOME;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected WatchedMovie() {
    }

    public WatchedMovie(User user, Movie movie, LocalDate watchedDate, Integer rating, String review, WatchPlace place) {
        this.user = user;
        this.movie = movie;
        this.createdAt = Instant.now();
        update(watchedDate, rating, review, place);
    }

    public void update(LocalDate watchedDate, Integer rating, String review, WatchPlace place) {
        this.watchedDate = watchedDate;
        this.rating = rating;
        this.review = (review == null || review.isBlank()) ? null : review.trim();
        this.place = place == null ? WatchPlace.HOME : place;
        this.updatedAt = Instant.now();
    }

    public boolean hasReview() {
        return review != null;
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

    public LocalDate getWatchedDate() {
        return watchedDate;
    }

    public Integer getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public WatchPlace getPlace() {
        return place;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
