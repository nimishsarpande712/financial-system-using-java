package com.jpmorgan.transaction.service;

import com.jpmorgan.transaction.dto.IncentiveResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IncentiveServiceTest {

    @Test
    void testCalculateIncentive_ApiDisabled_CreditAbove100() {
        IncentiveService incentiveService = new IncentiveService(
            "http://localhost:8081/api/incentives/calculate",
            false
        );

        IncentiveResponse response = incentiveService.calculateIncentive(
            1L,
            new BigDecimal("150.00"),
            "CREDIT"
        );

        assertNotNull(response);
        assertTrue(response.getApplied());
        assertEquals(new BigDecimal("1.50"), response.getIncentiveAmount());
        assertEquals("PERCENTAGE", response.getIncentiveType());
    }

    @Test
    void testCalculateIncentive_ApiDisabled_CreditBelow100() {
        IncentiveService incentiveService = new IncentiveService(
            "http://localhost:8081/api/incentives/calculate",
            false
        );

        IncentiveResponse response = incentiveService.calculateIncentive(
            1L,
            new BigDecimal("50.00"),
            "CREDIT"
        );

        assertNotNull(response);
        assertFalse(response.getApplied());
        assertEquals(BigDecimal.ZERO, response.getIncentiveAmount());
        assertEquals("NONE", response.getIncentiveType());
    }

    @Test
    void testCalculateIncentive_ApiDisabled_Debit() {
        IncentiveService incentiveService = new IncentiveService(
            "http://localhost:8081/api/incentives/calculate",
            false
        );

        IncentiveResponse response = incentiveService.calculateIncentive(
            1L,
            new BigDecimal("200.00"),
            "DEBIT"
        );

        assertNotNull(response);
        assertFalse(response.getApplied());
        assertEquals(BigDecimal.ZERO, response.getIncentiveAmount());
    }
}