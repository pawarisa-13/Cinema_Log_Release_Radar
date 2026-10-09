package com.cinemalog.mapper;

import java.util.Comparator;

import com.cinemalog.domain.entity.Genre;
import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.entity.UserProfile;
import com.cinemalog.dto.response.UserProfileResponse;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final MovieMapper movieMapper;

    public UserMapper(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public UserProfileResponse toProfile(User user) {
        UserProfile p = user.getProfile();
        return new UserProfileResponse(user.getId(), user.getEmail(), p.getDisplayName(), p.getBio(),
                p.getAvatarStyle(), p.getAvatarColor(),
                p.getFavoriteGenres().stream().sorted(Comparator.comparing(Genre::getName)).map(movieMapper::toGenre).toList());
    }
}
