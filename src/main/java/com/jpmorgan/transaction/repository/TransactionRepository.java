package com.jpmorgan.transaction.repository;

import com.jpmorgan.transaction.model.Transaction;
import com.jpmorgan.transaction.model.Transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status);
    
    List<Transaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    boolean existsByTransactionId(String transactionId);
}