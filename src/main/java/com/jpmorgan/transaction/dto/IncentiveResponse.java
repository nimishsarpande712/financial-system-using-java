package com.jpmorgan.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncentiveResponse {
    
    @JsonProperty("incentiveAmount")
    private BigDecimal incentiveAmount;
    
    @JsonProperty("incentiveType")
    private String incentiveType;
    
    @JsonProperty("applied")
    private Boolean applied;
}