package com.jpmorgan.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncentiveRequest {
    
    private Long userId;
    private BigDecimal transactionAmount;
    private String transactionType;
}