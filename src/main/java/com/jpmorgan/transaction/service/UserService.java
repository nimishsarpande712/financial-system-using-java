package com.jpmorgan.transaction.service;

import com.jpmorgan.transaction.dto.UserBalanceResponse;
import com.jpmorgan.transaction.exception.UserNotFoundException;
import com.jpmorgan.transaction.model.User;
import com.jpmorgan.transaction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public UserBalanceResponse getUserBalance(Long userId) {
        User user = findById(userId);
        return UserBalanceResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .balance(user.getBalance())
            .build();
    }

    @Transactional(readOnly = true)
    public List<UserBalanceResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> UserBalanceResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public User createUser(String username, String email, BigDecimal initialBalance) {
        User user = User.builder()
            .username(username)
            .email(email)
            .balance(initialBalance != null ? initialBalance : BigDecimal.ZERO)
            .build();
        
        User savedUser = userRepository.save(user);
        log.info("Created new user: {} with initial balance: {}", username, savedUser.getBalance());
        return savedUser;
    }

    @Transactional
    public void updateBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findWithLockingById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        userRepository.save(user);
        
        log.info("Updated balance for user {}: {} -> {}", userId, user.getBalance(), newBalance);
    }
}