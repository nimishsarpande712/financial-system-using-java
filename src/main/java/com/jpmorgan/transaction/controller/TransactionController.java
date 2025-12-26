package com.jpmorgan.transaction.controller;

import com.jpmorgan.transaction.dto.TransactionResponse;
import com.jpmorgan.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "APIs for querying transaction history")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieve all transactions in the system")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        log.info("Received request to get all transactions");
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user transactions", description = "Retrieve all transactions for a specific user")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        log.info("Received request to get transactions for user: {}", userId);
        List<TransactionResponse> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }
}