package com.jpmorgan.transaction.service;

import com.jpmorgan.transaction.dto.IncentiveResponse;
import com.jpmorgan.transaction.dto.TransactionMessage;
import com.jpmorgan.transaction.dto.TransactionResponse;
import com.jpmorgan.transaction.exception.DuplicateTransactionException;
import com.jpmorgan.transaction.exception.InsufficientBalanceException;
import com.jpmorgan.transaction.exception.UserNotFoundException;
import com.jpmorgan.transaction.model.Transaction;
import com.jpmorgan.transaction.model.User;
import com.jpmorgan.transaction.repository.TransactionRepository;
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
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final IncentiveService incentiveService;

    @Transactional
    public Transaction processTransaction(TransactionMessage message) {
        log.info("Processing transaction: {}", message.getTransactionId());

        // Check for duplicate transaction
        if (transactionRepository.existsByTransactionId(message.getTransactionId())) {
            throw new DuplicateTransactionException(
                "Transaction already exists: " + message.getTransactionId()
            );
        }

        // Validate user exists
        User user = userService.findById(message.getUserId());

        // Parse transaction type
        Transaction.TransactionType transactionType;
        try {
            transactionType = Transaction.TransactionType.valueOf(message.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + message.getType());
        }

        // Validate amount
        if (message.getAmount() == null || message.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        // Check balance for DEBIT transactions
        if (transactionType == Transaction.TransactionType.DEBIT) {
            if (user.getBalance().compareTo(message.getAmount()) < 0) {
                throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Current: %s, Required: %s",
                        user.getBalance(), message.getAmount())
                );
            }
        }

        // Calculate incentive
        IncentiveResponse incentiveResponse = incentiveService.calculateIncentive(
            message.getUserId(),
            message.getAmount(),
            message.getType()
        );

        // Create transaction
        Transaction transaction = Transaction.builder()
            .transactionId(message.getTransactionId())
            .user(user)
            .type(transactionType)
            .amount(message.getAmount())
            .description(message.getDescription())
            .incentiveApplied(incentiveResponse.getApplied())
            .incentiveAmount(incentiveResponse.getIncentiveAmount())
            .status(Transaction.TransactionStatus.COMPLETED)
            .build();

        // Calculate balance change
        BigDecimal balanceChange = message.getAmount();
        if (transactionType == Transaction.TransactionType.DEBIT) {
            balanceChange = balanceChange.negate();
        }

        // Add incentive if applicable
        if (incentiveResponse.getApplied() && incentiveResponse.getIncentiveAmount() != null) {
            balanceChange = balanceChange.add(incentiveResponse.getIncentiveAmount());
            log.info("Applied incentive of {} to transaction {}",
                incentiveResponse.getIncentiveAmount(), message.getTransactionId());
        }

        // Update user balance
        userService.updateBalance(user.getId(), balanceChange);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Successfully processed transaction {} for user {}. New balance: {}",
            savedTransaction.getTransactionId(),
            user.getId(),
            user.getBalance().add(balanceChange));

        return savedTransaction;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
            .map(this::toTransactionResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        // Verify user exists
        userService.findById(userId);
        
        return transactionRepository.findByUserId(userId).stream()
            .map(this::toTransactionResponse)
            .collect(Collectors.toList());
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
            .id(transaction.getId())
            .transactionId(transaction.getTransactionId())
            .userId(transaction.getUser().getId())
            .type(transaction.getType().name())
            .amount(transaction.getAmount())
            .description(transaction.getDescription())
            .incentiveApplied(transaction.getIncentiveApplied())
            .incentiveAmount(transaction.getIncentiveAmount())
            .status(transaction.getStatus().name())
            .timestamp(transaction.getTimestamp())
            .build();
    }
}