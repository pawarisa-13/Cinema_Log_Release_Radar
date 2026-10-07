package com.cinemalog.domain.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.cinemalog.domain.enums.AvatarColor;
import com.cinemalog.domain.enums.AvatarStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "display_name", nullable = false, length = 60)
    private String displayName;

    @Column(length = 200)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "avatar_style", nullable = false, length = 20)
    private AvatarStyle avatarStyle = AvatarStyle.BUN;

    @Enumerated(EnumType.STRING)
    @Column(name = "avatar_color", nullable = false, length = 20)
    private AvatarColor avatarColor = AvatarColor.PINK;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_favorite_genres",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> favoriteGenres = new LinkedHashSet<>();

    protected UserProfile() {
    }

    public UserProfile(String displayName) {
        this.displayName = displayName;
        this.updatedAt = Instant.now();
    }

    public void update(String displayName, String bio, AvatarStyle style, AvatarColor color) {
        this.displayName = displayName;
        this.bio = bio;
        this.avatarStyle = style;
        this.avatarColor = color;
        this.updatedAt = Instant.now();
    }

    public void replaceFavoriteGenres(Collection<Genre> genres) {
        this.favoriteGenres.clear();
        this.favoriteGenres.addAll(genres);
        this.updatedAt = Instant.now();
    }

    void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBio() {
        return bio;
    }

    public AvatarStyle getAvatarStyle() {
        return avatarStyle;
    }

    public AvatarColor getAvatarColor() {
        return avatarColor;
    }

    public Set<Genre> getFavoriteGenres() {
        return favoriteGenres;
    }
}
