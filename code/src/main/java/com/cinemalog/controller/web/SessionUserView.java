package com.cinemalog.controller.web;

import java.util.List;

import com.cinemalog.dto.response.GenreResponse;
import com.cinemalog.dto.response.UserProfileResponse;

public class SessionUserView {

    private final UserProfileResponse profile;

    public SessionUserView(UserProfileResponse profile) {
        this.profile = profile;
    }

    public String getDisplayName() {
        return profile.displayName();
    }

    public String getEmail() {
        return profile.email();
    }

    public String getMaskedEmail() {
        String email = profile.email();
        int at = email.indexOf('@');
        return at > 3 ? email.substring(0, 3) + "…" + email.substring(at) : email;
    }

    public String getBio() {
        return profile.bio();
    }

    public String getAvatarStyle() {
        return profile.avatarStyle().name();
    }

    public String getAvatarColor() {
        return profile.avatarColor().name();
    }

    public List<String> getFavoriteGenres() {
        return profile.favoriteGenres().stream().map(GenreResponse::name).toList();
    }

    public String getFavoriteGenresJoined() {
        return String.join("|", getFavoriteGenres());
    }
}
