package com.mm.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka listener for consuming Order events
 * Deserializes JSON messages into Order objects
 */
@Slf4j
@Service
public class OrderConsumerListener {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Primary listener for Order events
     * Consumes Order events from the order-events topic
     * @param orderJson JSON string representation of Order
     */
    @KafkaListener(topics = "${app.kafka.order-topic:order-events}",
                   groupId = "kafka-order-consumer-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrder(String orderJson) {
        log.debug("Attempting to consume order event from topic");
        try {
            // Deserialize JSON string to Order object
            Order order = objectMapper.readValue(orderJson, Order.class);
            log.info("✓ Order received: Order ID: {}, Customer: {}, Amount: {}, Status: {}",
                    order.getOrderId(), order.getCustomerName(), order.getTotalAmount(), order.getStatus());

            // Process order (add your business logic here)
            processOrder(order);
        } catch (Exception e) {
            log.error("✗ Error processing order event - Error: {}", e.getMessage(), e);
        }
    }

    /**
     * Process the received order
     * Add your business logic here (e.g., database operations, validations, etc.)
     * @param order Order object to process
     */
    private void processOrder(Order order) {
        log.debug("Processing order: {}", order.getOrderId());
        // TODO: Add order processing logic here
        // Examples:
        // - Save to database
        // - Send confirmation email
        // - Update inventory
        // - Publish to another topic
    }
}

