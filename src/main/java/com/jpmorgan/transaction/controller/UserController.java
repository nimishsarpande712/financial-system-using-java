package com.jpmorgan.transaction.controller;

import com.jpmorgan.transaction.dto.UserBalanceResponse;
import com.jpmorgan.transaction.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users and querying balances")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/balance")
    @Operation(summary = "Get user balance", description = "Retrieve the current balance for a specific user")
    public ResponseEntity<UserBalanceResponse> getUserBalance(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        log.info("Received request to get balance for user: {}", userId);
        UserBalanceResponse response = userService.getUserBalance(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with their balances")
    public ResponseEntity<List<UserBalanceResponse>> getAllUsers() {
        log.info("Received request to get all users");
        List<UserBalanceResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}