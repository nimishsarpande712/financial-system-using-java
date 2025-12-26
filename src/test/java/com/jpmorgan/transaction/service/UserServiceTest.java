package com.jpmorgan.transaction.service;

import com.jpmorgan.transaction.dto.UserBalanceResponse;
import com.jpmorgan.transaction.exception.UserNotFoundException;
import com.jpmorgan.transaction.model.User;
import com.jpmorgan.transaction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .balance(new BigDecimal("1000.00"))
            .build();
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void testGetUserBalance_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserBalanceResponse response = userService.getUserBalance(1L);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals(new BigDecimal("1000.00"), response.getBalance());
    }

    @Test
    void testCreateUser_Success() {
        User newUser = User.builder()
            .username("newuser")
            .email("new@example.com")
            .balance(BigDecimal.ZERO)
            .build();

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.createUser("newuser", "new@example.com", null);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(userRepository, times(1)).save(any(User.class));
    }
}