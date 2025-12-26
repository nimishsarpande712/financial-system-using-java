package com.jpmorgan.transaction.service;

import com.jpmorgan.transaction.dto.IncentiveResponse;
import com.jpmorgan.transaction.dto.TransactionMessage;
import com.jpmorgan.transaction.exception.DuplicateTransactionException;
import com.jpmorgan.transaction.exception.InsufficientBalanceException;
import com.jpmorgan.transaction.model.Transaction;
import com.jpmorgan.transaction.model.User;
import com.jpmorgan.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private IncentiveService incentiveService;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private TransactionMessage testMessage;
    private IncentiveResponse testIncentiveResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .balance(new BigDecimal("1000.00"))
            .build();

        testMessage = TransactionMessage.builder()
            .transactionId("txn-123")
            .userId(1L)
            .type("CREDIT")
            .amount(new BigDecimal("150.00"))
            .description("Test transaction")
            .build();

        testIncentiveResponse = IncentiveResponse.builder()
            .incentiveAmount(new BigDecimal("1.50"))
            .incentiveType("PERCENTAGE")
            .applied(true)
            .build();
    }

    @Test
    void testProcessTransaction_Credit_Success() {
        when(transactionRepository.existsByTransactionId("txn-123")).thenReturn(false);
        when(userService.findById(1L)).thenReturn(testUser);
        when(incentiveService.calculateIncentive(any(), any(), any())).thenReturn(testIncentiveResponse);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction result = transactionService.processTransaction(testMessage);

        assertNotNull(result);
        assertEquals("txn-123", result.getTransactionId());
        assertEquals(Transaction.TransactionType.CREDIT, result.getType());
        verify(userService, times(1)).updateBalance(eq(1L), any(BigDecimal.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testProcessTransaction_DuplicateTransaction() {
        when(transactionRepository.existsByTransactionId("txn-123")).thenReturn(true);

        assertThrows(DuplicateTransactionException.class,
            () -> transactionService.processTransaction(testMessage));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testProcessTransaction_InsufficientBalance() {
        TransactionMessage debitMessage = TransactionMessage.builder()
            .transactionId("txn-124")
            .userId(1L)
            .type("DEBIT")
            .amount(new BigDecimal("2000.00"))
            .description("Large withdrawal")
            .build();

        when(transactionRepository.existsByTransactionId("txn-124")).thenReturn(false);
        when(userService.findById(1L)).thenReturn(testUser);

        assertThrows(InsufficientBalanceException.class,
            () -> transactionService.processTransaction(debitMessage));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testProcessTransaction_InvalidAmount() {
        TransactionMessage invalidMessage = TransactionMessage.builder()
            .transactionId("txn-125")
            .userId(1L)
            .type("CREDIT")
            .amount(new BigDecimal("-100.00"))
            .description("Invalid amount")
            .build();

        when(transactionRepository.existsByTransactionId("txn-125")).thenReturn(false);
        when(userService.findById(1L)).thenReturn(testUser);

        assertThrows(IllegalArgumentException.class,
            () -> transactionService.processTransaction(invalidMessage));
    }
}