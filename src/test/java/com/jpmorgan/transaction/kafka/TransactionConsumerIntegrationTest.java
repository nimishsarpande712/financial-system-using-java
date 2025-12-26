package com.jpmorgan.transaction.kafka;

import com.jpmorgan.transaction.dto.TransactionMessage;
import com.jpmorgan.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"transaction-topic"}, brokerProperties = {
    "listeners=PLAINTEXT://localhost:9092",
    "port=9092"
})
class TransactionConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, TransactionMessage> kafkaTemplate;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testConsumeTransaction() throws Exception {
        TransactionMessage message = TransactionMessage.builder()
            .transactionId("test-txn-001")
            .userId(1L)
            .type("CREDIT")
            .amount(new BigDecimal("100.00"))
            .description("Test transaction")
            .build();

        kafkaTemplate.send("transaction-topic", message);

        // Wait for async processing
        TimeUnit.SECONDS.sleep(3);

        verify(transactionService, atLeastOnce()).processTransaction(any(TransactionMessage.class));
    }
}