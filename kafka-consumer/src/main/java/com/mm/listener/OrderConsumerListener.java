package com.mm.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

/**
 * Kafka listener for consuming Order events with error handling
 * Deserializes JSON messages into Order objects
 * Implements retry mechanism and Dead Letter Topic (DLT) handling
 */
@Slf4j
@Service
public class OrderConsumerListener {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Primary listener for Order events with automatic retry on failure
     *
     * @RetryableTopic configuration:
     * - attempts: 3 (1 initial + 2 retries)
     * - backoff: Exponential (1s → 2s → 4s, max 10s)
     * - autoCreateTopics: Automatically creates retry and DLT topics
     * - dltTopicSuffix: Dead Letter Topic naming convention
     *
     * @param orderJson JSON string representation of Order
     */
    @RetryableTopic(
        attempts = "3",                                          // Initial attempt + 2 retries
        backoff = @Backoff(
            delay = 1000,                                        // Start with 1 second delay
            multiplier = 2.0,                                    // Double the delay each time
            maxDelay = 10000                                     // Cap at 10 seconds
        ),
        autoCreateTopics = "true",                              // Auto-create retry topics
        include = {Exception.class},                            // Retry on any Exception
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltStrategy = org.springframework.kafka.retrytopic.DltStrategy.FAIL_ON_ERROR,
        retryTopicSuffix = "-retry",
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
        topics = "${app.kafka.order-topic:order-events}",
        groupId = "kafka-order-consumer-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
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
            log.error("✗ Error processing order event (will retry): {}", e.getMessage(), e);
        }
    }

    /**
     * Dead Letter Topic (DLT) handler for Order events
     * Called when an order message fails after all retry attempts
     *
     * @param orderJson The order message that failed
     * @param exception The exception that caused all retries to fail
     */
    @DltHandler
    public void handleOrderDlt(String orderJson, Exception exception) {
        log.error("✗ Order message sent to Dead Letter Topic (DLT) after all retries failed");
        log.error("   Failed order JSON: {}", orderJson);
        log.error("   Failure reason: {}", exception.getMessage());

        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            log.error("   Order ID: {}, Customer: {}", order.getOrderId(), order.getCustomerName());
        } catch (Exception e) {
            log.error("   Could not parse order: {}", e.getMessage());
        }

        // TODO: Add DLT handling logic here
        // Examples:
        // - Log to database for manual review/reprocessing
        // - Send alert to admin team
        // - Archive to separate DLT storage
        // - Publish to monitoring/alerting system
        // - Implement manual intervention workflow
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

