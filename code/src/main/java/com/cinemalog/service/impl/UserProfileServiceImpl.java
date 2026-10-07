package com.cinemalog.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.cinemalog.domain.entity.Genre;
import com.cinemalog.domain.entity.User;
import com.cinemalog.dto.request.FavoriteGenresRequest;
import com.cinemalog.dto.request.UpdateProfileRequest;
import com.cinemalog.dto.response.UserProfileResponse;
import com.cinemalog.exception.BusinessRuleException;
import com.cinemalog.exception.DuplicateResourceException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.UserMapper;
import com.cinemalog.repository.GenreRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.UserProfileService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final UserMapper userMapper;

    public UserProfileServiceImpl(UserRepository userRepository, GenreRepository genreRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return userMapper.toProfile(load(userId));
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = load(userId);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, userId)) {
            throw new DuplicateResourceException("Another account already uses this email.");
        }
        user.changeEmail(email);
        user.getProfile().update(request.displayName().trim(), blankToNull(request.bio()),
                request.avatarStyle(), request.avatarColor());
        if (request.favoriteGenreIds() != null) {
            user.getProfile().replaceFavoriteGenres(resolveGenres(request.favoriteGenreIds()));
        }
        return userMapper.toProfile(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateFavoriteGenres(Long userId, FavoriteGenresRequest request) {
        User user = load(userId);
        user.getProfile().replaceFavoriteGenres(resolveGenres(request.genreIds()));
        return userMapper.toProfile(user);
    }

    @Override
    public boolean needsOnboarding(Long userId) {
        return load(userId).getProfile().getFavoriteGenres().isEmpty();
    }

    private User load(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " was not found."));
    }

    private List<Genre> resolveGenres(List<Integer> ids) {
        List<Genre> genres = genreRepository.findAllById(new HashSet<>(ids));
        if (genres.size() != new HashSet<>(ids).size()) {
            throw new BusinessRuleException("One of the selected genres does not exist.");
        }
        return genres;
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
