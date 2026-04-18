package com.mm.controller;

import com.mm.dto.Order;
import com.mm.service.OrderProducerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Order event publishing
 * Provides endpoints to publish Order events to Kafka
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderProducerService orderProducerService;

    @Value("${app.kafka.order-topic:order-events}")
    private String orderTopic;

    /**
     * Publish an Order event to Kafka
     * @param order Order object to publish
     * @return ResponseEntity with success/failure message
     */
    @PostMapping("/publish")
    public ResponseEntity<?> publishOrder(@RequestBody Order order) {
        try {
            log.debug("Received HTTP request to publish order - Order ID: {}", order.getOrderId());
            orderProducerService.sendOrder(orderTopic, order);
            log.info("HTTP response: Order published successfully - Order ID: {}", order.getOrderId());
            return ResponseEntity.ok(new OrderResponse("Order published successfully", order.getOrderId(), orderTopic));
        } catch (Exception e) {
            log.error("HTTP error: Failed to publish order - Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to publish order", e.getMessage()));
        }
    }

    // Inner classes for request/response
    @Getter
    public static class OrderResponse {
        public String status;
        public String orderId;
        public String topic;

        public OrderResponse(String status, String orderId, String topic) {
            this.status = status;
            this.orderId = orderId;
            this.topic = topic;
        }
    }

    @Getter
    public static class ErrorResponse {
        public String error;
        public String detail;

        public ErrorResponse(String error, String detail) {
            this.error = error;
            this.detail = detail;
        }
    }
}

