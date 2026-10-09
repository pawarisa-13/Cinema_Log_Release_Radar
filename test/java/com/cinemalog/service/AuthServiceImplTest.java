package com.cinemalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cinemalog.domain.entity.User;
import com.cinemalog.dto.request.RegisterRequest;
import com.cinemalog.exception.DuplicateResourceException;
import com.cinemalog.mapper.UserMapper;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    AuthServiceImpl service;

    @Test
    void rejectsAnEmailThatIsAlreadyUsed() {
        when(userRepository.existsByEmailIgnoreCase("paw@kkumail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(new RegisterRequest("Paw", "Paw@kkumail.com", "secret1")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void storesAHashedPasswordAndAProfile() {
        when(userRepository.existsByEmailIgnoreCase("paw@kkumail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("$hashed$");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.register(new RegisterRequest("  Paw  ", " Paw@KKUmail.com ", "secret1"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo("paw@kkumail.com");
        assertThat(saved.getValue().getPasswordHash()).isEqualTo("$hashed$");
        assertThat(saved.getValue().getProfile().getDisplayName()).isEqualTo("Paw");
    }
}
