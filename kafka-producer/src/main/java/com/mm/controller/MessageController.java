package com.mm.controller;

import com.mm.service.KafkaProducerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Send a message to a Kafka topic
     * @param topic Topic name
     * @param message Message to send
     * @return ResponseEntity with success/failure message
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam String topic,
            @RequestParam String message) {
        try {
            log.debug("Received HTTP request to send message - Topic: {}, Message: {}", topic, message);
            kafkaProducerService.sendMessage(topic, message);
            log.info("HTTP response: Message sent successfully to topic: {}", topic);
            return ResponseEntity.ok(new MessageResponse("Message sent successfully", topic, message));
        } catch (Exception e) {
            log.error("HTTP error: Failed to send message - Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to send message", e.getMessage()));
        }
    }

    // Inner classes for request/response
    @Getter
    public static class MessageResponse {
        public String status;
        public String topic;
        public String message;

        public MessageResponse(String status, String topic, String message) {
            this.status = status;
            this.topic = topic;
            this.message = message;
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
