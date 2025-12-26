package com.jpmorgan.transaction.service;

import com.jpmorgan.transaction.dto.IncentiveRequest;
import com.jpmorgan.transaction.dto.IncentiveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class IncentiveService {

    private final RestTemplate restTemplate;
    private final String incentiveApiUrl;
    private final boolean incentiveApiEnabled;

    public IncentiveService(
            @Value("${incentive.api.url}") String incentiveApiUrl,
            @Value("${incentive.api.enabled}") boolean incentiveApiEnabled) {
        this.restTemplate = new RestTemplate();
        this.incentiveApiUrl = incentiveApiUrl;
        this.incentiveApiEnabled = incentiveApiEnabled;
    }

    public IncentiveResponse calculateIncentive(Long userId, BigDecimal amount, String transactionType) {
        if (!incentiveApiEnabled) {
            log.info("Incentive API disabled, using default calculation");
            return calculateDefaultIncentive(amount, transactionType);
        }

        try {
            IncentiveRequest request = IncentiveRequest.builder()
                .userId(userId)
                .transactionAmount(amount)
                .transactionType(transactionType)
                .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<IncentiveRequest> entity = new HttpEntity<>(request, headers);

            log.info("Calling external incentive API for user {} with amount {}", userId, amount);
            ResponseEntity<IncentiveResponse> response = restTemplate.postForEntity(
                incentiveApiUrl,
                entity,
                IncentiveResponse.class
            );

            if (response.getBody() != null) {
                log.info("Received incentive response: {}", response.getBody());
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("Error calling incentive API, falling back to default calculation: {}", e.getMessage());
        }

        return calculateDefaultIncentive(amount, transactionType);
    }

    private IncentiveResponse calculateDefaultIncentive(BigDecimal amount, String transactionType) {
        // Default incentive logic: 1% for CREDIT transactions above 100
        BigDecimal incentiveAmount = BigDecimal.ZERO;
        boolean applied = false;
        String incentiveType = "NONE";

        if ("CREDIT".equalsIgnoreCase(transactionType) && amount.compareTo(new BigDecimal("100")) > 0) {
            incentiveAmount = amount.multiply(new BigDecimal("0.01"))
                .setScale(2, RoundingMode.HALF_UP);
            applied = true;
            incentiveType = "PERCENTAGE";
            log.info("Applied default incentive of {} for amount {}", incentiveAmount, amount);
        }

        return IncentiveResponse.builder()
            .incentiveAmount(incentiveAmount)
            .incentiveType(incentiveType)
            .applied(applied)
            .build();
    }
}