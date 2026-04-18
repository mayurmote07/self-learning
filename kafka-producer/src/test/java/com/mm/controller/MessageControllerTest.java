package com.mm.controller;

import com.mm.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MessageController
 */
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Test
    void testSendMessageSuccess() throws Exception {
        // Arrange
        String topic = "test-topic";
        String message = "Hello Kafka";
        doNothing().when(kafkaProducerService).sendMessage(topic, message);

        // Act & Assert
        mockMvc.perform(post("/api/messages/send")
                .param("topic", topic)
                .param("message", message))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Message sent successfully"))
                .andExpect(jsonPath("$.topic").value(topic))
                .andExpect(jsonPath("$.message").value(message));

        verify(kafkaProducerService, times(1)).sendMessage(topic, message);
    }

    @Test
    void testSendMessageWithSpecialCharacters() throws Exception {
        // Arrange
        String topic = "test-topic";
        String message = "Hello 😊 Kafka! @#$%";
        doNothing().when(kafkaProducerService).sendMessage(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/messages/send")
                .param("topic", topic)
                .param("message", message))
                .andExpect(status().isOk());

        verify(kafkaProducerService, times(1)).sendMessage(topic, message);
    }

    @Test
    void testSendMessageMissingTopic() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/messages/send")
                .param("message", "Hello"))
                .andExpect(status().isBadRequest());

        verify(kafkaProducerService, never()).sendMessage(anyString(), anyString());
    }

    @Test
    void testSendMessageMissingMessage() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/messages/send")
                .param("topic", "test"))
                .andExpect(status().isBadRequest());

        verify(kafkaProducerService, never()).sendMessage(anyString(), anyString());
    }

    @Test
    void testSendMessageEmptyValues() throws Exception {
        // Arrange
        doNothing().when(kafkaProducerService).sendMessage("", "");

        // Act & Assert
        mockMvc.perform(post("/api/messages/send")
                .param("topic", "")
                .param("message", ""))
                .andExpect(status().isOk());

        verify(kafkaProducerService, times(1)).sendMessage("", "");
    }

    @Test
    void testSendMessageWithLongMessage() throws Exception {
        // Arrange
        String topic = "test-topic";
        String longMessage = "A".repeat(1000);
        doNothing().when(kafkaProducerService).sendMessage(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/messages/send")
                .param("topic", topic)
                .param("message", longMessage))
                .andExpect(status().isOk());

        verify(kafkaProducerService, times(1)).sendMessage(topic, longMessage);
    }
}

