package com.cinemalog.mapper;

import com.cinemalog.domain.entity.UserProfile;
import com.cinemalog.domain.entity.WatchedMovie;
import com.cinemalog.dto.response.DiaryEntryResponse;
import com.cinemalog.dto.response.ReviewResponse;

import org.springframework.stereotype.Component;

@Component
public class DiaryMapper {

    private final MovieMapper movieMapper;

    public DiaryMapper(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public DiaryEntryResponse toResponse(WatchedMovie w) {
        return new DiaryEntryResponse(w.getId(), movieMapper.toSummary(w.getMovie()), w.getWatchedDate(),
                w.getRating(), w.getReview(), w.getPlace(), w.getCreatedAt(), w.getUpdatedAt());
    }

    public ReviewResponse toReview(WatchedMovie w, Long viewerId) {
        UserProfile p = w.getUser().getProfile();
        boolean mine = viewerId != null && viewerId.equals(w.getUser().getId());
        return new ReviewResponse(w.getId(), p.getDisplayName(), p.getAvatarStyle(), p.getAvatarColor(),
                w.getRating(), w.getReview(), w.getWatchedDate(), mine);
    }
}
