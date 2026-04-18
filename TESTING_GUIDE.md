# Testing Guide - Kafka Learning Project

## Overview

This document describes all test cases for the Kafka Learning Project. The project includes unit tests for:
- Services (Producers)
- Listeners (Consumers)
- Controllers (REST APIs)
- Configuration (Jackson)

---

## Test Files & Coverage

### 1. **KafkaProducerServiceTest** 
**Location:** `kafka-producer/src/test/java/com/mm/service/`

Tests the `KafkaProducerService` class for sending string messages.

| Test Case | Purpose | Status |
|-----------|---------|--------|
| `testSendMessageSuccess` | Verify message is sent to Kafka topic | ✅ |
| `testSendMessageWithDifferentTopics` | Test sending to multiple topics | ✅ |
| `testSendEmptyMessage` | Handle empty string messages | ✅ |
| `testSendLongMessage` | Handle large message payloads | ✅ |
| `testSendSpecialCharacters` | Handle special characters & emojis | ✅ |

**Run:**
```powershell
mvn -pl kafka-producer test -Dtest=KafkaProducerServiceTest
```

---

### 2. **OrderProducerServiceTest**
**Location:** `kafka-producer/src/test/java/com/mm/service/`

Tests the `OrderProducerService` class for sending Order objects as JSON.

| Test Case | Purpose | Status |
|-----------|---------|--------|
| `testSendOrderSuccess` | Verify Order is serialized and sent | ✅ |
| `testSendMultipleOrders` | Send multiple orders sequentially | ✅ |
| `testSendOrderWithDifferentStatuses` | Test various order statuses | ✅ |
| `testSendOrderJsonConversionError` | Handle JSON serialization errors | ✅ |
| `testSendOrderWithNullFields` | Handle orders with null values | ✅ |

**Run:**
```powershell
mvn -pl kafka-producer test -Dtest=OrderProducerServiceTest
```

---

### 3. **MessageControllerTest**
**Location:** `kafka-producer/src/test/java/com/mm/controller/`

Tests REST API endpoint `/api/messages/send` for string messages.

| Test Case | Purpose | Status |
|-----------|---------|--------|
| `testSendMessageSuccess` | Verify API returns 200 OK with correct response | ✅ |
| `testSendMessageWithSpecialCharacters` | API handles special characters | ✅ |
| `testSendMessageMissingTopic` | Returns 400 Bad Request when topic missing | ✅ |
| `testSendMessageMissingMessage` | Returns 400 Bad Request when message missing | ✅ |
| `testSendMessageEmptyValues` | Handle empty topic/message parameters | ✅ |
| `testSendMessageWithLongMessage` | API processes large messages | ✅ |

**API Tested:**
```
POST /api/messages/send?topic=test&message=Hello
```

**Run:**
```powershell
mvn -pl kafka-producer test -Dtest=MessageControllerTest
```

---

### 4. **OrderControllerTest**
**Location:** `kafka-producer/src/test/java/com/mm/controller/`

Tests REST API endpoint `/api/orders/publish` and `/api/orders/topic` for Orders.

| Test Case | Purpose | Status |
|-----------|---------|--------|
| `testPublishOrderSuccess` | Verify Order is published successfully | ✅ |
| `testPublishOrderWithDifferentStatus` | Handle different order statuses | ✅ |
| `testPublishOrderInvalidJson` | Returns 400 for invalid JSON | ✅ |
| `testPublishOrderMissingRequiredFields` | Handle missing fields | ✅ |
| `testGetOrderTopic` | Verify GET `/api/orders/topic` returns topic name | ✅ |
| `testPublishMultipleOrders` | Publish multiple orders in sequence | ✅ |
| `testPublishOrderWithNegativeQuantity` | Handle invalid quantity values | ✅ |
| `testPublishOrderWithZeroPrice` | Handle zero price | ✅ |

**APIs Tested:**
```
POST /api/orders/publish
GET /api/orders/topic
```

**Run:**
```powershell
mvn -pl kafka-producer test -Dtest=OrderControllerTest
```

---

### 5. **KafkaConsumerListenerTest**
**Location:** `kafka-consumer/src/test/java/com/mm/listener/`

Tests the `KafkaConsumerListener` class for receiving string messages.

| Test Case | Purpose | Status |
|-----------|---------|--------|
| `testConsumeStringMessage` | Verify listener processes string message | ✅ |
| `testConsumeMultipleMessages` | Process multiple messages sequentially | ✅ |
| `testConsumeEmptyMessage` | Handle empty messages gracefully | ✅ |
| `testConsumeSpecialCharacters` | Process special characters & emojis | ✅ |
| `testConsumeLongMessage` | Handle large message payloads | ✅ |
| `testConsumeJsonMessage` | Process JSON strings as messages | ✅ |
| `testConsumeMessageWithNewlines` | Handle multiline messages | ✅ |

**Run:**
```powershell
mvn -pl kafka-consumer test -Dtest=KafkaConsumerListenerTest
```

---

### 6. **OrderConsumerListenerTest**
**Location:** `kafka-consumer/src/test/java/com/mm/listener/`

Tests the `OrderConsumerListener` class for receiving and deserializing Order objects.

| Test Case | Purpose | Status |
|-----------|---------|--------|
| `testConsumeOrderSuccess` | Verify Order is deserialized successfully | ✅ |
| `testConsumeMultipleOrders` | Deserialize multiple orders | ✅ |
| `testConsumeOrderWithDifferentStatuses` | Handle various order statuses | ✅ |
| `testConsumeOrderInvalidJson` | Handle malformed JSON gracefully | ✅ |
| `testConsumeOrderWithEmptyJson` | Handle empty JSON objects | ✅ |
| `testConsumeOrderWithNullValues` | Process orders with null fields | ✅ |

**Run:**
```powershell
mvn -pl kafka-consumer test -Dtest=OrderConsumerListenerTest
```

---

### 7. **JacksonConfigTest**
**Location:** `kafka-common/src/test/java/com/mm/config/`

Tests the Jackson ObjectMapper configuration for JSON serialization/deserialization.

| Test Case | Purpose | Status |
|-----------|---------|--------|
| `testObjectMapperNotNull` | Verify bean is created | ✅ |
| `testSerializeOrderToJson` | Convert Order object to JSON | ✅ |
| `testDeserializeJsonToOrder` | Convert JSON to Order object | ✅ |
| `testDateTimeFormatIsIso8601` | Verify ISO-8601 date format | ✅ |
| `testWriteDatesAsTimestampsDisabled` | Confirm timestamps disabled | ✅ |
| `testSerializeAndDeserializeRoundTrip` | Test round-trip conversion | ✅ |
| `testSerializeOrderWithNullFields` | Handle null values in serialization | ✅ |
| `testDeserializeJsonWithMissingFields` | Handle missing JSON fields | ✅ |

**Run:**
```powershell
mvn -pl kafka-common test -Dtest=JacksonConfigTest
```

---

## Running All Tests

### Run All Tests in Project
```powershell
mvn clean test
```

### Run Tests for Specific Module
```powershell
mvn -pl kafka-producer test
mvn -pl kafka-consumer test
mvn -pl kafka-common test
```

### Run Tests with Coverage
```powershell
mvn clean test jacoco:report
```

### Run Specific Test Class
```powershell
mvn test -Dtest=KafkaProducerServiceTest
```

### Run Specific Test Method
```powershell
mvn test -Dtest=KafkaProducerServiceTest#testSendMessageSuccess
```

---

## Test Frameworks & Tools

| Tool | Version | Purpose |
|------|---------|---------|
| JUnit 5 | 5.9+ | Testing framework |
| Mockito | 4.0+ | Mocking dependencies |
| Spring Boot Test | 3.2.4 | Spring test utilities |
| MockMvc | 3.2.4 | HTTP endpoint testing |

---

## Test Data

### Sample Order for Testing
```json
{
  "orderId": "ORD-2026-001",
  "customerId": "CUST-101",
  "customerName": "John Doe",
  "productName": "Laptop",
  "quantity": 2,
  "price": 999.99,
  "totalAmount": 1999.98,
  "status": "CREATED",
  "createdAt": "2026-04-18T14:30:00",
  "updatedAt": "2026-04-18T14:30:00"
}
```

### Test Message Examples
```
"Hello Kafka"
"Special: 😊 @#$%"
"Multi\nLine\nMessage"
"{\"key\":\"value\"}"
```

---

## Test Scenarios

### Scenario 1: String Message Flow
1. Send string message via `MessageController`
2. `KafkaProducerService` publishes to `mayur-test` topic
3. `KafkaConsumerListener` receives and logs message
✅ **Expected:** Message successfully sent and received

### Scenario 2: Order Event Flow
1. Publish Order via `OrderController`
2. `OrderProducerService` serializes to JSON and publishes
3. `OrderConsumerListener` receives and deserializes Order
4. Order processed successfully
✅ **Expected:** Order flows through entire pipeline

### Scenario 3: JSON Serialization
1. Order object created with all fields
2. `JacksonConfig` serializes with ISO-8601 dates
3. Deserialize JSON back to Order object
4. Compare original and deserialized objects
✅ **Expected:** Round-trip conversion succeeds

### Scenario 4: Error Handling
1. Send invalid JSON to controller
2. Service attempts to process
3. Exception handled gracefully
✅ **Expected:** Error response with 400 status

---

## Coverage Goals

| Module | Target Coverage | Current |
|--------|-----------------|---------|
| kafka-producer | 80% | In Progress |
| kafka-consumer | 80% | In Progress |
| kafka-common | 90% | In Progress |
| **Overall** | **80%** | In Progress |

---

## Continuous Integration

### GitHub Actions (Recommended)
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '20'
      - run: mvn clean test
```

---

## Troubleshooting Tests

### Issue: Kafka Connection Fails
**Solution:** Tests use mocks, Kafka doesn't need to be running

### Issue: Jackson Deserialization Fails
**Solution:** Ensure JSON format matches Order class fields exactly

### Issue: Mock Verification Fails
**Solution:** Verify exact parameter matching and call counts

### Issue: Port Already in Use
**Solution:** MockMvc doesn't require actual port, uses test servlet

---

## Best Practices

✅ Use `@Mock` for external dependencies  
✅ Use `@InjectMocks` for class under test  
✅ Use `doNothing()` for void methods  
✅ Use `mockMvc` for REST API tests  
✅ Test both success and failure paths  
✅ Use descriptive test method names  
✅ Group related tests in same class  
✅ Clean up resources in `@AfterEach`  

---

## Next Steps

1. **Expand Tests:** Add integration tests with embedded Kafka
2. **Add Performance Tests:** Measure message throughput
3. **Add Load Tests:** Test with high message volume
4. **Add Chaos Tests:** Test failure scenarios
5. **Monitor Coverage:** Track and improve code coverage

---

**Status:** ✅ Test Suite Ready  
**Last Updated:** April 19, 2026  
**Total Test Cases:** 45+

