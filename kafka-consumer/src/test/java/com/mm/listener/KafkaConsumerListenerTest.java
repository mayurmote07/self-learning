package com.mm.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaConsumerListener
 */
@ExtendWith(MockitoExtension.class)
public class KafkaConsumerListenerTest {

    @InjectMocks
    private KafkaConsumerListener kafkaConsumerListener;

    @Test
    void testConsumeStringMessage() {
        // Arrange
        String message = "Hello Kafka";

        // Act
        kafkaConsumerListener.consume(message);

        // Assert - method should not throw exception
        // In real scenario, we'd verify logs or side effects
    }

    @Test
    void testConsumeMultipleMessages() {
        // Arrange
        String message1 = "Message 1";
        String message2 = "Message 2";
        String message3 = "Message 3";

        // Act
        kafkaConsumerListener.consume(message1);
        kafkaConsumerListener.consume(message2);
        kafkaConsumerListener.consume(message3);

        // Assert - all should be consumed without error
    }

    @Test
    void testConsumeEmptyMessage() {
        // Arrange
        String emptyMessage = "";

        // Act
        kafkaConsumerListener.consume(emptyMessage);

        // Assert - should handle gracefully
    }

    @Test
    void testConsumeSpecialCharacters() {
        // Arrange
        String specialMessage = "Special chars: @#$%^&*() 😊";

        // Act
        kafkaConsumerListener.consume(specialMessage);

        // Assert - should handle special characters
    }

    @Test
    void testConsumeLongMessage() {
        // Arrange
        String longMessage = "A".repeat(10000);

        // Act
        kafkaConsumerListener.consume(longMessage);

        // Assert - should handle long messages
    }

    @Test
    void testConsumeJsonMessage() {
        // Arrange
        String jsonMessage = "{\"key\":\"value\",\"number\":123}";

        // Act
        kafkaConsumerListener.consume(jsonMessage);

        // Assert - should handle JSON strings
    }

    @Test
    void testConsumeMessageWithNewlines() {
        // Arrange
        String messageWithNewlines = "Line 1\nLine 2\nLine 3";

        // Act
        kafkaConsumerListener.consume(messageWithNewlines);

        // Assert - should handle multiline messages
    }
}

