package com.mm.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerListener {

    @Value("${app.kafka.topic:mayur-test}")
    private String topic;

    /**
     * Listen to messages from a Kafka topic
     * @param message Message received from topic
     */
    @KafkaListener(topics = "${app.kafka.topic:mayur-test}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.debug("Attempting to consume message from topic: {}", topic);
        try {
            log.info("✓ Message received: {}", message);
        } catch (Exception e) {
            log.error("✗ Error processing message: {}", e.getMessage(), e);
        }
    }
}

