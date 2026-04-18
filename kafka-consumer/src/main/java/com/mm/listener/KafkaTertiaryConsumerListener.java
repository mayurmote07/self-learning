package com.mm.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaTertiaryConsumerListener {

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${app.kafka.topic:mayur-test}")
    private String topic;

    /**
     * Tertiary listener for the same Kafka topic and group as primary listener
     * Uses the SAME consumer group to demonstrate load-balancing behavior
     * Messages will be shared between primary and tertiary listeners based on partition assignment
     * @param message Message received from topic
     */
    @KafkaListener(topics = "${app.kafka.topic:mayur-test}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTertiary(String message) {
        log.debug("Attempting to consume message from topic (Tertiary): {} with group: {}", topic, groupId);
        try {
            log.info("✓ [TERTIARY] Message received: {}", message);
        } catch (Exception e) {
            log.error("✗ [TERTIARY] Error processing message: {}", e.getMessage(), e);
        }
    }
}

