package com.jpmorgan.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    
    private Long id;
    private String transactionId;
    private Long userId;
    private String type;
    private BigDecimal amount;
    private String description;
    private Boolean incentiveApplied;
    private BigDecimal incentiveAmount;
    private String status;
    private LocalDateTime timestamp;
}