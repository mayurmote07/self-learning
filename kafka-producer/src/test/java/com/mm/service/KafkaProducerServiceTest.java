package com.mm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaProducerService
 */
@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService producerService;

    @BeforeEach
    void setUp() {
        // kafkaTemplate is already injected via @Mock
    }

    @Test
    void testSendMessageSuccess() {
        // Arrange
        String topic = "test-topic";
        String message = "Hello Kafka";
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // Act
        producerService.sendMessage(topic, message);

        // Assert
        verify(kafkaTemplate, times(1)).send(topic, message);
    }

    @Test
    void testSendMessageWithDifferentTopics() {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // Act
        producerService.sendMessage("topic1", "message1");
        producerService.sendMessage("topic2", "message2");
        producerService.sendMessage("topic3", "message3");

        // Assert
        verify(kafkaTemplate, times(3)).send(anyString(), anyString());
        verify(kafkaTemplate, times(1)).send("topic1", "message1");
        verify(kafkaTemplate, times(1)).send("topic2", "message2");
        verify(kafkaTemplate, times(1)).send("topic3", "message3");
    }

    @Test
    void testSendEmptyMessage() {
        // Arrange
        String topic = "test-topic";
        String emptyMessage = "";
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // Act
        producerService.sendMessage(topic, emptyMessage);

        // Assert
        verify(kafkaTemplate, times(1)).send(topic, emptyMessage);
    }

    @Test
    void testSendLongMessage() {
        // Arrange
        String topic = "test-topic";
        String longMessage = "A".repeat(1000);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // Act
        producerService.sendMessage(topic, longMessage);

        // Assert
        verify(kafkaTemplate, times(1)).send(topic, longMessage);
    }

    @Test
    void testSendSpecialCharacters() {
        // Arrange
        String topic = "test-topic";
        String specialMessage = "Hello 😊 Kafka! @#$%^&*()";
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // Act
        producerService.sendMessage(topic, specialMessage);

        // Assert
        verify(kafkaTemplate, times(1)).send(topic, specialMessage);
    }
}

