package com.cinemalog.domain.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "watchlist_items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}))
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "added_at", nullable = false, updatable = false)
    private Instant addedAt;

    protected WatchlistItem() {
    }

    public WatchlistItem(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
        this.addedAt = Instant.now();
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

    public Instant getAddedAt() {
        return addedAt;
    }
}
