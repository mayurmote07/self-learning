package com.mm.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerListener {

    @Value("${app.kafka.topic:mayur-test}")
    private String topic;

    /**
     * Listen to messages from a Kafka topic with automatic retry on failure
     *
     * @RetryableTopic: Automatically retries on exception
     * - maxAttempts: Total attempts (initial + retries)
     * - backoff: Exponential backoff strategy
     * - enabled: Can be disabled via config
     *
     * @param message Message received from topic
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
    @KafkaListener(topics = "${app.kafka.topic:mayur-test}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.debug("Attempting to consume message from topic: {}", topic);
        try {
            log.info("✓ Message received: {}", message);
            // Business logic here - if it throws an exception, it will be retried
        } catch (Exception e) {
            log.error("✗ Error processing message (will retry): {}", e.getMessage(), e);
        }
    }

    /**
     * Dead Letter Topic (DLT) handler
     * This method is called when message fails after all retry attempts
     *
     * @param message Message that failed all retries
     * @param exception The exception that caused the failure
     */
    @DltHandler
    public void handleDlt(String message, Exception exception) {
        log.error("✗ Message sent to Dead Letter Topic (DLT) after all retries failed");
        log.error("   Failed message: {}", message);
        log.error("   Reason: {}", exception.getMessage());

        // TODO: Add DLT handling logic here
        // Examples:
        // - Log to database for manual review
        // - Send alert notification
        // - Archive to separate storage
        // - Publish to monitoring system
    }
}

