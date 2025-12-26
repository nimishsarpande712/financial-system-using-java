package com.jpmorgan.transaction.kafka;

import com.jpmorgan.transaction.dto.TransactionMessage;
import com.jpmorgan.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(
        topics = "${kafka.topic.transactions}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransaction(
            @Payload TransactionMessage message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received transaction message from topic: {}, partition: {}, offset: {}",
            topic, partition, offset);
        log.info("Transaction details - ID: {}, User: {}, Type: {}, Amount: {}",
            message.getTransactionId(),
            message.getUserId(),
            message.getType(),
            message.getAmount());

        try {
            transactionService.processTransaction(message);
            log.info("Successfully processed transaction: {}", message.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}",
                message.getTransactionId(), e.getMessage(), e);
            // In production, you might want to send to a dead letter queue
            throw e; // Re-throw to trigger retry mechanism
        }
    }
}