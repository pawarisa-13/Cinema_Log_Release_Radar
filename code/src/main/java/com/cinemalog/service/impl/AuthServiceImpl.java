package com.cinemalog.service.impl;

import java.util.Locale;

import com.cinemalog.domain.entity.User;
import com.cinemalog.domain.entity.UserProfile;
import com.cinemalog.dto.request.RegisterRequest;
import com.cinemalog.dto.response.UserProfileResponse;
import com.cinemalog.exception.DuplicateResourceException;
import com.cinemalog.mapper.UserMapper;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("An account with this email already exists. Try logging in.");
        }
        User user = new User(email, passwordEncoder.encode(request.password()));
        user.attachProfile(new UserProfile(request.displayName().trim()));
        return userMapper.toProfile(userRepository.save(user));
    }
}
