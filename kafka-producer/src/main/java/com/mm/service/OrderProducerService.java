package com.mm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for publishing Order events to Kafka
 * Handles serialization of Order objects to JSON format
 */
@Slf4j
@Service
public class OrderProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Send an Order event to Kafka topic
     * @param topic Topic name for order events
     * @param order Order object to send
     */
    public void sendOrder(String topic, Order order) {
        log.debug("Attempting to send order to topic: {} - Order ID: {}", topic, order.getOrderId());

        try {
            // Serialize Order object to JSON string
            String orderJson = objectMapper.writeValueAsString(order);

            kafkaTemplate.send(topic, order.getOrderId(), orderJson)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("✗ Failed to send order to topic: {} - Order ID: {} - Error: {}",
                            topic, order.getOrderId(), exception.getMessage(), exception);
                    } else {
                        log.info("✓ Order sent successfully to topic: {} - Order ID: {}, Partition: {}, Offset: {}",
                            topic, order.getOrderId(), result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    }
                });
        } catch (Exception e) {
            log.error("✗ Error serializing order - Order ID: {} - Error: {}",
                order.getOrderId(), e.getMessage(), e);
        }
    }
}

