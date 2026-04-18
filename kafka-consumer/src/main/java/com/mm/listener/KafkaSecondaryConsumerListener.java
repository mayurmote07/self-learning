package com.mm.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSecondaryConsumerListener {

    @Value("${app.kafka.topic:mayur-test}")
    private String topic;

    /**
     * Secondary listener for the same Kafka topic
     * Uses a different consumer group to independently process all messages
     * @param message Message received from topic
     */
    @KafkaListener(topics = "${app.kafka.topic:mayur-test}", groupId = "kafka-secondary-consumer-group")
    public void consumeSecondary(String message) {
        log.debug("Attempting to consume message from topic (Secondary): {}", topic);
        try {
            log.info("✓ [SECONDARY] Message received: {}", message);
        } catch (Exception e) {
            log.error("✗ [SECONDARY] Error processing message: {}", e.getMessage(), e);
        }
    }
}

