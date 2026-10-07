package com.cinemalog.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "collections")
public class MovieCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("addedAt DESC")
    private List<CollectionMovie> items = new ArrayList<>();

    protected MovieCollection() {
    }

    public MovieCollection(User user, String name, String description) {
        this.user = user;
        this.createdAt = Instant.now();
        rename(name, description);
    }

    public void rename(String name, String description) {
        this.name = name.trim();
        this.description = (description == null || description.isBlank()) ? null : description.trim();
    }

    public boolean contains(Long movieId) {
        return items.stream().anyMatch(i -> i.getMovie().getId().equals(movieId));
    }

    public boolean addMovie(Movie movie) {
        if (contains(movie.getId())) {
            return false;
        }
        items.add(0, new CollectionMovie(this, movie));
        return true;
    }

    public boolean removeMovie(Long movieId) {
        return items.removeIf(i -> i.getMovie().getId().equals(movieId));
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<CollectionMovie> getItems() {
        return items;
    }
}
