package com.conaveg.cona.service;

import com.conaveg.cona.dto.UserCreateDTO;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.model.User;
import com.conaveg.cona.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateDTO userCreateDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUserName("testuser");
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("plainPassword123");

        user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$encodedPasswordHash");
    }

    @Test
    void saveUser_ShouldEncryptPassword() {
        // Given
        String encodedPassword = "$2a$10$encodedPasswordHash";
        when(passwordEncoder.encode("plainPassword123")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDTO result = userService.saveUser(userCreateDTO);

        // Then
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("plainPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldEncryptPasswordWhenProvided() {
        // Given
        String encodedPassword = "$2a$10$newEncodedPasswordHash";
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("plainPassword123")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDTO result = userService.updateUser(1L, userCreateDTO);

        // Then
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("plainPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldNotEncryptPasswordWhenNotProvided() {
        // Given
        userCreateDTO.setPassword(null);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDTO result = userService.updateUser(1L, userCreateDTO);

        // Then
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void validatePassword_ShouldReturnTrueForValidPassword() {
        // Given
        String rawPassword = "plainPassword123";
        String encodedPassword = "$2a$10$encodedPasswordHash";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // When
        boolean result = userService.validatePassword(rawPassword, encodedPassword);

        // Then
        assertTrue(result);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void validatePassword_ShouldReturnFalseForInvalidPassword() {
        // Given
        String rawPassword = "wrongPassword";
        String encodedPassword = "$2a$10$encodedPasswordHash";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // When
        boolean result = userService.validatePassword(rawPassword, encodedPassword);

        // Then
        assertFalse(result);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }
}
