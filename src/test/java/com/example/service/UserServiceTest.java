package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@test.com");
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.createUser(user);
        assertNotNull(result);
        assertEquals("Test User", result.getFullName());
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);
        assertNotNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        User result = userService.getUserById(1L);
        assertNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser_Found() {
        User updatedDetails = new User();
        updatedDetails.setFullName("Updated");
        updatedDetails.setEmail("upd@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updatedDetails);
        
        assertNotNull(result);
        assertEquals("Updated", result.getFullName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        User result = userService.updateUser(1L, new User());
        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_Found() {
        when(userRepository.existsById(1L)).thenReturn(true);
        boolean result = userService.deleteUser(1L);
        assertTrue(result);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        boolean result = userService.deleteUser(1L);
        assertFalse(result);
        verify(userRepository, never()).deleteById(1L);
    }
}

