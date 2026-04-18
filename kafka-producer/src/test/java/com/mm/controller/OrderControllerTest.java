package com.mm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.dto.Order;
import com.mm.service.OrderProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrderController
 */
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderProducerService orderProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .orderId("ORD-2026-001")
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
    void testPublishOrderSuccess() throws Exception {
        // Arrange
        doNothing().when(orderProducerService).sendOrder(any(Order.class));

        // Act & Assert
        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Order published successfully"))
                .andExpect(jsonPath("$.orderId").value("ORD-2026-001"))
                .andExpect(jsonPath("$.topic").value("order-events"));

        verify(orderProducerService, times(1)).sendOrder(any(Order.class));
    }

    @Test
    void testPublishOrderWithDifferentStatus() throws Exception {
        // Arrange
        testOrder.setStatus("PROCESSING");
        doNothing().when(orderProducerService).sendOrder(any(Order.class));

        // Act & Assert
        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk());

        verify(orderProducerService, times(1)).sendOrder(any(Order.class));
    }

    @Test
    void testPublishOrderInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(orderProducerService, never()).sendOrder(any(Order.class));
    }

    @Test
    void testPublishOrderMissingRequiredFields() throws Exception {
        // Arrange
        Order invalidOrder = Order.builder()
                .orderId("ORD-002")
                // Missing other required fields
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetOrderTopic() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders/topic")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topic").value("order-events"));
    }

    @Test
    void testPublishMultipleOrders() throws Exception {
        // Arrange
        doNothing().when(orderProducerService).sendOrder(any(Order.class));

        Order order1 = testOrder.builder().orderId("ORD-001").build();
        Order order2 = testOrder.builder().orderId("ORD-002").build();

        // Act & Assert
        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)))
                .andExpect(status().isOk());

        verify(orderProducerService, times(2)).sendOrder(any(Order.class));
    }

    @Test
    void testPublishOrderWithNegativeQuantity() throws Exception {
        // Arrange
        testOrder.setQuantity(-5);
        doNothing().when(orderProducerService).sendOrder(any(Order.class));

        // Act & Assert
        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk());
    }

    @Test
    void testPublishOrderWithZeroPrice() throws Exception {
        // Arrange
        testOrder.setPrice(0.0);
        doNothing().when(orderProducerService).sendOrder(any(Order.class));

        // Act & Assert
        mockMvc.perform(post("/api/orders/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk());
    }
}

