package com.mm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.dto.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderProducerService
 */
@ExtendWith(MockitoExtension.class)
public class OrderProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderProducerService orderProducerService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .orderId("ORD-001")
                .customerId("CUST-101")
                .customerName("John Doe")
                .productName("Laptop")
                .quantity(2)
                .price(999.99)
                .totalAmount(1999.98)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testSendOrderSuccess() throws JsonProcessingException {
        // Arrange
        String orderJson = "{\"orderId\":\"ORD-001\"}";
        when(objectMapper.writeValueAsString(testOrder)).thenReturn(orderJson);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(null);

        // Act
        orderProducerService.sendOrder(testOrder);

        // Assert
        verify(objectMapper, times(1)).writeValueAsString(testOrder);
        verify(kafkaTemplate, times(1)).send("order-events", "ORD-001", orderJson);
    }

    @Test
    void testSendMultipleOrders() throws JsonProcessingException {
        // Arrange
        String orderJson = "{\"orderId\":\"ORD-001\"}";
        when(objectMapper.writeValueAsString(any(Order.class))).thenReturn(orderJson);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(null);

        Order order1 = testOrder;
        Order order2 = testOrder.builder().orderId("ORD-002").build();
        Order order3 = testOrder.builder().orderId("ORD-003").build();

        // Act
        orderProducerService.sendOrder(order1);
        orderProducerService.sendOrder(order2);
        orderProducerService.sendOrder(order3);

        // Assert
        verify(kafkaTemplate, times(3)).send(anyString(), anyString(), anyString());
    }

    @Test
    void testSendOrderWithDifferentStatuses() throws JsonProcessingException {
        // Arrange
        String orderJson = "{\"status\":\"PROCESSING\"}";
        when(objectMapper.writeValueAsString(any(Order.class))).thenReturn(orderJson);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(null);

        Order processingOrder = testOrder.builder().status("PROCESSING").build();
        Order completedOrder = testOrder.builder().status("COMPLETED").build();

        // Act
        orderProducerService.sendOrder(processingOrder);
        orderProducerService.sendOrder(completedOrder);

        // Assert
        verify(kafkaTemplate, times(2)).send(anyString(), anyString(), anyString());
    }

    @Test
    void testSendOrderJsonConversionError() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(testOrder))
                .thenThrow(new JsonProcessingException("JSON conversion error") {});

        // Act & Assert
        try {
            orderProducerService.sendOrder(testOrder);
        } catch (Exception e) {
            verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
        }
    }

    @Test
    void testSendOrderWithNullFields() throws JsonProcessingException {
        // Arrange
        Order orderWithNulls = Order.builder()
                .orderId("ORD-004")
                .customerId(null)
                .customerName(null)
                .build();

        String orderJson = "{\"orderId\":\"ORD-004\"}";
        when(objectMapper.writeValueAsString(orderWithNulls)).thenReturn(orderJson);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(null);

        // Act
        orderProducerService.sendOrder(orderWithNulls);

        // Assert
        verify(kafkaTemplate, times(1)).send("order-events", "ORD-004", orderJson);
    }
}

