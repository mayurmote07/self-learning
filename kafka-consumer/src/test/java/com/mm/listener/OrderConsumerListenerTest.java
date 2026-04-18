package com.mm.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.dto.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderConsumerListener
 */
@ExtendWith(MockitoExtension.class)
public class OrderConsumerListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderConsumerListener orderConsumerListener;

    private Order testOrder;
    private String orderJson;

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

        orderJson = "{\"orderId\":\"ORD-001\",\"customerId\":\"CUST-101\"}";
    }

    @Test
    void testConsumeOrderSuccess() throws Exception {
        // Arrange
        when(objectMapper.readValue(orderJson, Order.class)).thenReturn(testOrder);

        // Act
        orderConsumerListener.consumeOrder(orderJson);

        // Assert
        verify(objectMapper, times(1)).readValue(orderJson, Order.class);
    }

    @Test
    void testConsumeMultipleOrders() throws Exception {
        // Arrange
        String orderJson2 = "{\"orderId\":\"ORD-002\"}";
        String orderJson3 = "{\"orderId\":\"ORD-003\"}";

        when(objectMapper.readValue(anyString(), eq(Order.class)))
                .thenReturn(testOrder);

        // Act
        orderConsumerListener.consumeOrder(orderJson);
        orderConsumerListener.consumeOrder(orderJson2);
        orderConsumerListener.consumeOrder(orderJson3);

        // Assert
        verify(objectMapper, times(3)).readValue(anyString(), eq(Order.class));
    }

    @Test
    void testConsumeOrderWithDifferentStatuses() throws Exception {
        // Arrange
        String createdJson = "{\"status\":\"CREATED\"}";
        String processingJson = "{\"status\":\"PROCESSING\"}";
        String completedJson = "{\"status\":\"COMPLETED\"}";

        when(objectMapper.readValue(anyString(), eq(Order.class)))
                .thenReturn(testOrder);

        // Act
        orderConsumerListener.consumeOrder(createdJson);
        orderConsumerListener.consumeOrder(processingJson);
        orderConsumerListener.consumeOrder(completedJson);

        // Assert
        verify(objectMapper, times(3)).readValue(anyString(), eq(Order.class));
    }

    @Test
    void testConsumeOrderInvalidJson() throws Exception {
        // Arrange
        String invalidJson = "{invalid}";
        when(objectMapper.readValue(invalidJson, Order.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        // Act & Assert
        try {
            orderConsumerListener.consumeOrder(invalidJson);
        } catch (Exception e) {
            verify(objectMapper, times(1)).readValue(invalidJson, Order.class);
        }
    }

    @Test
    void testConsumeOrderWithEmptyJson() throws Exception {
        // Arrange
        String emptyJson = "{}";
        when(objectMapper.readValue(emptyJson, Order.class)).thenReturn(testOrder);

        // Act
        orderConsumerListener.consumeOrder(emptyJson);

        // Assert
        verify(objectMapper, times(1)).readValue(emptyJson, Order.class);
    }

    @Test
    void testConsumeOrderWithNullValues() throws Exception {
        // Arrange
        String orderWithNulls = "{\"orderId\":null}";
        Order nullOrder = Order.builder().orderId(null).build();
        when(objectMapper.readValue(orderWithNulls, Order.class)).thenReturn(nullOrder);

        // Act
        orderConsumerListener.consumeOrder(orderWithNulls);

        // Assert
        verify(objectMapper, times(1)).readValue(orderWithNulls, Order.class);
    }
}

