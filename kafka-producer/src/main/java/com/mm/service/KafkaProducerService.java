package com.mm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Send a message to a Kafka topic
     * @param topic Topic name
     * @param message Message to send
     */
    public void sendMessage(String topic, String message) {
        log.debug("Attempting to send message to topic: {} - Message content: {}", topic, message);

        kafkaTemplate.send(topic, message)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("✗ Failed to send message to topic: {} - Error: {}",
                        topic, exception.getMessage(), exception);
                } else {
                    log.info("✓ Message sent successfully to topic: {} - Partition: {}, Offset: {}",
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }
}
