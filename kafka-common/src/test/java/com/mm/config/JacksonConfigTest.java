package com.mm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mm.dto.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JacksonConfig
 */
public class JacksonConfigTest {

    private ObjectMapper objectMapper;
    private JacksonConfig jacksonConfig;

    @BeforeEach
    void setUp() {
        jacksonConfig = new JacksonConfig();
        objectMapper = jacksonConfig.objectMapper();
    }

    @Test
    void testObjectMapperNotNull() {
        // Assert
        assertNotNull(objectMapper, "ObjectMapper should not be null");
    }

    @Test
    void testSerializeOrderToJson() throws Exception {
        // Arrange
        Order order = Order.builder()
                .orderId("ORD-001")
                .customerId("CUST-101")
                .customerName("John Doe")
                .productName("Laptop")
                .quantity(2)
                .price(999.99)
                .totalAmount(1999.98)
                .status("CREATED")
                .createdAt(LocalDateTime.of(2026, 4, 18, 14, 30, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 18, 14, 30, 0))
                .build();

        // Act
        String json = objectMapper.writeValueAsString(order);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("ORD-001"));
        assertTrue(json.contains("John Doe"));
        assertTrue(json.contains("2026-04-18"));
    }

    @Test
    void testDeserializeJsonToOrder() throws Exception {
        // Arrange
        String json = "{\"orderId\":\"ORD-001\",\"customerId\":\"CUST-101\"," +
                "\"customerName\":\"John Doe\",\"productName\":\"Laptop\"," +
                "\"quantity\":2,\"price\":999.99,\"totalAmount\":1999.98," +
                "\"status\":\"CREATED\"," +
                "\"createdAt\":\"2026-04-18T14:30:00\"," +
                "\"updatedAt\":\"2026-04-18T14:30:00\"}";

        // Act
        Order order = objectMapper.readValue(json, Order.class);

        // Assert
        assertNotNull(order);
        assertEquals("ORD-001", order.getOrderId());
        assertEquals("John Doe", order.getCustomerName());
        assertEquals("Laptop", order.getProductName());
        assertEquals(2, order.getQuantity());
        assertEquals(999.99, order.getPrice());
    }

    @Test
    void testDateTimeFormatIsIso8601() throws Exception {
        // Arrange
        Order order = Order.builder()
                .orderId("ORD-001")
                .createdAt(LocalDateTime.of(2026, 4, 18, 14, 30, 0))
                .build();

        // Act
        String json = objectMapper.writeValueAsString(order);

        // Assert
        assertTrue(json.contains("2026-04-18T14:30:00"),
                "Date should be in ISO-8601 format");
        assertFalse(json.matches(".*\\d{13}.*"),
                "Should not contain timestamp in milliseconds");
    }

    @Test
    void testWriteDatesAsTimestampsDisabled() throws Exception {
        // Assert
        assertTrue(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) == false ||
                !objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "WRITE_DATES_AS_TIMESTAMPS should be disabled");
    }

    @Test
    void testSerializeAndDeserializeRoundTrip() throws Exception {
        // Arrange
        Order originalOrder = Order.builder()
                .orderId("ORD-001")
                .customerId("CUST-101")
                .customerName("John Doe")
                .productName("Laptop")
                .quantity(2)
                .price(999.99)
                .totalAmount(1999.98)
                .status("CREATED")
                .createdAt(LocalDateTime.of(2026, 4, 18, 14, 30, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 18, 14, 30, 0))
                .build();

        // Act
        String json = objectMapper.writeValueAsString(originalOrder);
        Order deserializedOrder = objectMapper.readValue(json, Order.class);

        // Assert
        assertEquals(originalOrder.getOrderId(), deserializedOrder.getOrderId());
        assertEquals(originalOrder.getCustomerName(), deserializedOrder.getCustomerName());
        assertEquals(originalOrder.getQuantity(), deserializedOrder.getQuantity());
        assertEquals(originalOrder.getPrice(), deserializedOrder.getPrice());
    }

    @Test
    void testSerializeOrderWithNullFields() throws Exception {
        // Arrange
        Order orderWithNulls = Order.builder()
                .orderId("ORD-001")
                .customerId(null)
                .customerName(null)
                .build();

        // Act
        String json = objectMapper.writeValueAsString(orderWithNulls);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("ORD-001"));
    }

    @Test
    void testDeserializeJsonWithMissingFields() throws Exception {
        // Arrange
        String json = "{\"orderId\":\"ORD-001\"}";

        // Act
        Order order = objectMapper.readValue(json, Order.class);

        // Assert
        assertNotNull(order);
        assertEquals("ORD-001", order.getOrderId());
        assertNull(order.getCustomerName());
    }
}

