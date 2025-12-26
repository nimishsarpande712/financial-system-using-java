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
public class UserBalanceResponse {
    
    private Long userId;
    private String username;
    private String email;
    private BigDecimal balance;
}