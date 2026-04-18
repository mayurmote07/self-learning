# Kafka Learning Project 🚀

A hands-on, beginner-friendly project to learn **Apache Kafka** concepts including producers, consumers, and messaging patterns using **Spring Boot**.

**What You'll Learn:**
- ✅ Kafka fundamentals (topics, partitions, consumer groups)
- ✅ Building Kafka producers and consumers with Spring Boot
- ✅ Sending simple string messages
- ✅ Serializing/deserializing complex objects (JSON)
- ✅ Real-world order processing example
- ✅ Error handling with retries and Dead Letter Topics

---

## 📋 Table of Contents

1. [What is Kafka?](#what-is-kafka)
2. [Setup Kafka](#setup-kafka-on-your-machine)
3. [Project Structure](#project-structure)
4. [Quick Start](#quick-start-guide)
5. [Understanding Producers](#understanding-kafka-producers)
6. [Understanding Consumers](#understanding-kafka-consumers)
7. [Message Serialization](#message-serialization--deserialization)
8. [Error Handling](#error-handling-with-retries-and-dead-letter-topics)
9. [API Examples](#api-examples)
10. [Kafka Learning Notes](#kafka-learning-notes)

---

## What is Kafka?

### The Basics

**Apache Kafka** is a distributed messaging system that allows applications to communicate asynchronously:

```
┌─────────┐         Topic: "order-events"        ┌──────────┐
│Producer │ ─────────────────────────────────────▶ Consumer │
│(Sender) │         (Message Queue)                (Listener)│
└─────────┘                                       └──────────┘
```

### Key Concepts

| Concept | Explanation |
|---------|-------------|
| **Topic** | A channel/category for messages (e.g., "order-events") |
| **Producer** | Application that sends messages to a topic |
| **Consumer** | Application that receives messages from a topic |
| **Partition** | Sub-divisions of a topic for parallel processing |
| **Consumer Group** | Group of consumers sharing the workload |
| **Message/Record** | The actual data being sent (string, JSON, etc.) |

### Why Kafka?

1. **Decoupling**: Producers don't wait for consumers to be ready
2. **Scalability**: Handle millions of messages efficiently
3. **Durability**: Messages are stored on disk, never lost
4. **Real-time**: Process data as it arrives

---

## Setup Kafka on Your Machine

Choose **one** setup method below:

### Option 1: Docker Setup (⭐ Recommended for Beginners)

**Why Docker?** Faster, cleaner, no complex configuration.

#### Prerequisites
- Docker and Docker Compose installed

#### Steps

1. **Start Kafka in Docker**
   ```powershell
   cd src\main\resources
   docker compose up -d
   ```

2. **Verify it's running**
   ```powershell
   docker ps
   ```

3. **Create a test topic** (optional)
   ```powershell
   docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --create --topic mayur-test --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
   ```

4. **Stop Kafka when done**
   ```powershell
   docker compose down -v
   ```

#### Docker Compose Configuration

The file `src/main/resources/docker-compose.yaml` contains:
- **Kafka Image**: apache/kafka:4.2.0 (latest stable)
- **Port**: 9092 (for connections)
- **KRaft Mode**: No ZooKeeper needed
- **Data Storage**: Persists in `kafka_data` volume

---

### Option 2: Manual Setup (Windows, KRaft Mode)

**Why Manual?** Learn how Kafka works under the hood.

#### Prerequisites
- Windows machine
- Java installed

#### Steps

1. **Download Kafka**
   - Download `kafka_2.13-4.2.0` from [kafka.apache.org](https://kafka.apache.org)
   - Extract to `C:\kafka`

2. **Generate Cluster ID**
   ```bat
   cd C:\kafka
   bin\windows\kafka-storage.bat random-uuid
   ```
   Copy the generated ID.

3. **Format Storage**
   ```bat
   bin\windows\kafka-storage.bat format -t <CLUSTER_ID> -c config\server.properties --standalone
   ```

4. **Start Kafka Server**
   ```bat
   bin\windows\kafka-server-start.bat config\server.properties
   ```
   Wait for: `[KafkaServer id=1] started`

5. **Create Topic** (in a new terminal)
   ```bat
   bin\windows\kafka-topics.bat --create --topic mayur-test --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
   ```

6. **Test with Console Producer** (in another new terminal)
   ```bat
   bin\windows\kafka-console-producer.bat --topic mayur-test --bootstrap-server localhost:9092
   > Hello Kafka
   > Type messages here...
   ```

7. **Test with Console Consumer** (in yet another new terminal)
   ```bat
   bin\windows\kafka-console-consumer.bat --topic mayur-test --group my-group --bootstrap-server localhost:9092
   ```

---

## Project Structure

This is a **multi-module Maven project** with three modules:

```
kafka-learning/
│
├── kafka-common/                     (Shared Models)
│   └── src/main/java/com/mm/dto/
│       └── Order.java                (Order data model)
│
├── kafka-producer/                   (Spring Boot App - Port 8080)
│   ├── src/main/java/com/mm/
│   │   ├── KafkaProducerApplication.java    (Entry point)
│   │   ├── config/
│   │   │   └── JacksonConfig.java           (JSON configuration)
│   │   ├── service/
│   │   │   ├── KafkaProducerService.java    (Send strings)
│   │   │   └── OrderProducerService.java    (Send objects)
│   │   └── controller/
│   │       ├── MessageController.java       (REST API for strings)
│   │       └── OrderController.java         (REST API for objects)
│   └── src/main/resources/
│       └── application.yaml           (Configuration)
│
├── kafka-consumer/                   (Spring Boot App - Port 8081)
│   ├── src/main/java/com/mm/
│   │   ├── KafkaConsumerApplication.java    (Entry point)
│   │   ├── config/
│   │   │   └── JacksonConfig.java           (JSON configuration)
│   │   └── listener/
│   │       ├── KafkaConsumerListener.java         (Listen for strings)
│   │       └── OrderConsumerListener.java        (Listen for objects)
│   └── src/main/resources/
│       └── application.yaml           (Configuration)
│
└── pom.xml                            (Parent - defines all modules)
```

### What Goes Where?

| Module | Purpose | Port | Runs |
|--------|---------|------|------|
| **kafka-producer** | Sends messages to Kafka | 8080 | `mvn -pl kafka-producer spring-boot:run` |
| **kafka-consumer** | Receives messages from Kafka | 8081 | `mvn -pl kafka-consumer spring-boot:run` |
| **kafka-common** | Shared data models (Order.java) | — | Dependency only |

---

## Quick Start Guide

### 1. Start Kafka

```powershell
# Docker way (recommended)
cd src\main\resources
docker compose up -d

# Or start manual Kafka server if using Option 2
```

### 2. Build the Project

```powershell
mvn clean package
```

### 3. Start Producer (Terminal 1)

```powershell
mvn -pl kafka-producer spring-boot:run
```

Output: `Application started on http://localhost:8080`

### 4. Start Consumer (Terminal 2)

```powershell
mvn -pl kafka-consumer spring-boot:run
```

Output: `Application started on http://localhost:8081`

### 5. Send a Message

Open your browser or Postman and call:

```
GET http://localhost:8080/api/messages/send?topic=mayur-test&message=HelloWorld
```

### 6. Check Consumer Logs (Terminal 2)

You should see something like:
```
Received message: HelloWorld
```

🎉 **Congratulations!** You've sent your first Kafka message!

---

## Understanding Kafka Producers

### What is a Producer?

A **Producer** is a Spring Boot application that:
1. Creates/sends messages
2. Publishes them to a Kafka topic
3. Doesn't care if anyone is listening

### How to Create a Producer

#### Step 1: Configure Kafka in `application.yaml`

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092  # Where is Kafka?
```

#### Step 2: Create a Service Class

```java
@Service
@Slf4j
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendMessage(String topic, String message) {
        // Send message to Kafka topic
        kafkaTemplate.send(topic, message);
        log.info("Message sent: {}", message);
    }
}
```

**What's happening?**
- `KafkaTemplate`: Spring's helper to send messages
- `kafkaTemplate.send()`: Publishes message to topic
- Producer doesn't wait for response ⚡

#### Step 3: Expose via REST API

```java
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    @Autowired
    private KafkaProducerService producerService;
    
    @PostMapping("/send")
    public Map<String, String> sendMessage(
        @RequestParam String topic,
        @RequestParam String message) {
        
        producerService.sendMessage(topic, message);
        
        return Map.of(
            "status", "Message sent successfully",
            "topic", topic,
            "message", message
        );
    }
}
```

### Testing Your Producer

```powershell
# Terminal 1: Start producer
mvn -pl kafka-producer spring-boot:run

# Terminal 2: Send a message
curl "http://localhost:8080/api/messages/send?topic=mytest&message=Hello"

# Output:
# {
#   "status": "Message sent successfully",
#   "topic": "mytest",
#   "message": "Hello"
# }
```

---

## Understanding Kafka Consumers

### What is a Consumer?

A **Consumer** is a Spring Boot application that:
1. Listens to messages from a Kafka topic
2. Processes/stores them automatically
3. Tracks which messages it has read

### How to Create a Consumer

#### Step 1: Configure Kafka in `application.yaml`

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: my-app-group      # Unique identifier for this consumer
      auto-offset-reset: earliest  # Read from beginning if new
      enable-auto-commit: true     # Automatically mark as read
```

**Important Settings:**
- **group-id**: Identifies your consumer group (can have multiple consumers in same group)
- **auto-offset-reset**: What to do when starting fresh
- **enable-auto-commit**: Automatically remember what we've read

#### Step 2: Create a Listener Class

```java
@Component
@Slf4j
public class KafkaConsumerListener {
    
    @KafkaListener(topics = "mayur-test")
    public void consume(String message) {
        log.info("📨 Received message: {}", message);
        // Do something with the message
    }
}
```

**What's happening?**
- `@KafkaListener`: Annotation that tells Spring "listen to this topic"
- `consume()`: Automatically called when a message arrives
- Method runs on separate thread ⚡

### Testing Your Consumer

```powershell
# Terminal 1: Start consumer
mvn -pl kafka-consumer spring-boot:run
# Output: Started KafkaConsumerApplication

# Terminal 2: Start producer
mvn -pl kafka-producer spring-boot:run

# Terminal 3: Send messages
curl "http://localhost:8080/api/messages/send?topic=mayur-test&message=Test1"
curl "http://localhost:8080/api/messages/send?topic=mayur-test&message=Test2"

# Terminal 1 output:
# 📨 Received message: Test1
# 📨 Received message: Test2
```

### Consumer Groups (Advanced)

You can have **multiple consumers** listening to the same topic:

```yaml
# Consumer 1
spring.kafka.consumer.group-id: order-processing-group

# Consumer 2 (same group)
spring.kafka.consumer.group-id: order-processing-group
```

**What happens?**
- Both consumers share the workload
- Message 1 → Consumer 1
- Message 2 → Consumer 2
- Each consumer gets a fair share

---

## Error Handling with Retries and Dead Letter Topics

### The Problem

**Messages can fail during processing.** Without proper error handling:
- Failed messages are lost
- Transient errors (network issues) aren't retried
- No way to track/fix permanently failed messages

**Solution**: Use `@RetryTopic` and `@DltHandler` for automatic retry and Dead Letter Topic (DLT) management.

### How It Works

```
Normal Message
         ↓
   [Consumer]
         ↓
    [Success] ✅
    
    OR
    
Failed Message (Transient Error)
         ↓
   [Consumer] → Error!
         ↓
  [Retry Topic-1] (1 sec delay)
         ↓
   [Consumer] → Error!
         ↓
  [Retry Topic-2] (2 sec delay)
         ↓
   [Consumer] → Error!
         ↓
  [Dead Letter Topic]
         ↓
   [@DltHandler] → Manual Review ⚠️
```

### Configuration Example

```java
@RetryableTopic(
    attempts = "3",                                    // 1 initial + 2 retries
    backoff = @Backoff(
        delay = 1000,                                  // Start: 1 second
        multiplier = 2.0,                              // Next: 2 seconds, then 4 seconds
        maxDelay = 10000                               // Cap at 10 seconds
    ),
    autoCreateTopics = "true",                        // Auto-create retry + DLT topics
    include = {Exception.class},                      // Retry on any Exception
    retryTopicSuffix = "-retry",                      // Retry topic name pattern
    dltTopicSuffix = "-dlt"                           // DLT topic name pattern
)
@KafkaListener(topics = "order-events", groupId = "order-group")
public void consumeOrder(String orderJson) {
    try {
        Order order = objectMapper.readValue(orderJson, Order.class);
        log.info("✓ Order processed: {}", order.getOrderId());
        // Process order...
    } catch (Exception e) {
        log.error("✗ Error processing order (will retry): {}", e.getMessage(), e);
        throw e;  // Re-throw to trigger retry mechanism
    }
}
```

### Dead Letter Topic Handler

```java
/**
 * Called when message fails after all retry attempts
 * This is your last chance to handle the error
 */
@DltHandler
public void handleOrderDlt(String orderJson, Exception exception) {
    log.error("✗ Order sent to DLT after all retries failed");
    log.error("   Order: {}", orderJson);
    log.error("   Reason: {}", exception.getMessage());
    
    // TODO: Add DLT handling logic:
    // - Save to database for manual review
    // - Send alert to admin team
    // - Archive to separate DLT storage
    // - Publish to monitoring system
}
```

### Retry Behavior Explained

**Initial Attempt + Retry Attempts:**
```
Attempt 1: Immediate (no delay)
    ↓ Error → Scheduled for retry
    
Attempt 2: After 1 second delay
    ↓ Error → Scheduled for retry
    
Attempt 3: After 2 second delay
    ↓ Error → Sent to DLT
    
Dead Letter Topic Handler invoked
```

**Backoff Strategy (Exponential):**
| Attempt | Delay | Why? |
|---------|-------|------|
| 1 | Immediate | Try right away |
| 2 | 1 second | Short delay for transient issues |
| 3 | 2 seconds | Longer delay for slower recovery |
| (more) | Max 10s | Don't overwhelm the system |

### Topic Naming Convention

With `@RetryableTopic`, Spring automatically creates topics:

```
Original Topic: order-events
├── Retry Topic 1: order-events-retry-0
├── Retry Topic 2: order-events-retry-1
└── Dead Letter Topic: order-events-dlt
```

### When Retries Happen

✅ **Retries ARE triggered for:**
- Network timeouts
- Temporary database unavailability
- JSON parsing errors
- Transient service failures
- Any `Exception` thrown in the handler

❌ **Retries are NOT triggered for:**
- Business logic errors (if caught and logged)
- Messages that are intentionally skipped
- Errors in `@DltHandler` method

### Configuration in application.yaml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: kafka-order-consumer-group
      auto-offset-reset: earliest
      
# Configure retry behavior (optional overrides)
management:
  endpoints:
    web:
      exposure:
        include: health,metrics  # Monitor retries
```

### Monitoring Retries

View retry topic metrics:

```powershell
# List all topics including retry topics
docker exec kafka /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Output:
# order-events
# order-events-retry-0
# order-events-retry-1
# order-events-dlt
# mayur-test
```

### Example: Order Processing with Error Handling

```java
@Service
@Slf4j
public class OrderConsumerListener {
    
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000),
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = "order-events", groupId = "order-processing")
    public void processOrder(String orderJson) {
        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            
            // Simulate processing that might fail
            validateOrder(order);           // May throw exception
            saveToDatabase(order);          // May throw exception
            sendConfirmationEmail(order);   // May throw exception
            
            log.info("✓ Order processed successfully: {}", order.getOrderId());
        } catch (JsonProcessingException e) {
            log.error("✗ JSON parsing failed (will retry): {}", e.getMessage());
            throw new RuntimeException("JSON parsing error", e);
        } catch (DatabaseException e) {
            log.error("✗ Database error (will retry): {}", e.getMessage());
            throw e;  // Transient error, retry
        } catch (Exception e) {
            log.error("✗ Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Unexpected error", e);
        }
    }
    
    @DltHandler
    public void handleFailedOrder(String orderJson, Exception exception) {
        log.error("⚠️  Order failed after all retries: {}", orderJson);
        
        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            // Save to failed_orders table for manual review
            failedOrderRepository.save(new FailedOrder(
                order.getOrderId(),
                orderJson,
                exception.getMessage(),
                LocalDateTime.now()
            ));
            // Send alert
            alertService.notifyAdmin("Order " + order.getOrderId() + " failed");
        } catch (Exception e) {
            log.error("Error handling DLT message: {}", e.getMessage());
        }
    }
    
    private void validateOrder(Order order) throws ValidationException {
        if (order.getTotalAmount() <= 0) {
            throw new ValidationException("Invalid order amount");
        }
    }
    
    private void saveToDatabase(Order order) throws DatabaseException {
        // Database operation that might fail temporarily
    }
    
    private void sendConfirmationEmail(Order order) throws Exception {
        // Email operation
    }
}
```

### Best Practices

✅ **DO:**
- Re-throw exceptions to trigger retries
- Use exponential backoff for transient errors
- Log detailed information in DLT handler
- Save failed messages for manual review
- Alert on DLT messages
- Set reasonable max retry counts

❌ **DON'T:**
- Catch and silently ignore exceptions
- Retry on permanent errors (bad data)
- Set retry delays too short (< 100ms)
- Ignore DLT messages
- Retry infinite times
- Block in consumer handlers

---

### The Problem

**Kafka only understands strings/bytes.** But we want to send objects like:

```json
{
  "orderId": "ORD-001",
  "customerName": "John Doe",
  "price": 99.99
}
```

**Solution**: Serialize ↔ Deserialize using Jackson (JSON library)

### Serialization (Producer Side)

**Serialization** = Converting Java Object → JSON String

```java
@Service
@Slf4j
public class OrderProducerService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;  // Jackson mapper
    
    public void sendOrder(Order order) throws JsonProcessingException {
        // Step 1: Convert Order object to JSON string
        String orderJson = objectMapper.writeValueAsString(order);
        
        // Step 2: Send JSON string to Kafka
        kafkaTemplate.send("order-events", order.getOrderId(), orderJson);
        
        log.info("✉️ Order sent: {}", orderJson);
    }
}
```

**Process:**
```
Order object {orderId: "ORD-001"}
          ↓
      Serialization (writeValueAsString)
          ↓
JSON string {"orderId":"ORD-001"}
          ↓
Send to Kafka Topic
```

### Deserialization (Consumer Side)

**Deserialization** = Converting JSON String → Java Object

```java
@Component
@Slf4j
public class OrderConsumerListener {
    
    @Autowired
    private ObjectMapper objectMapper;  // Jackson mapper
    
    @KafkaListener(topics = "order-events")
    public void consumeOrder(String orderJson) throws JsonProcessingException {
        // Step 1: Convert JSON string back to Order object
        Order order = objectMapper.readValue(orderJson, Order.class);
        
        // Step 2: Now you can use the Order object
        log.info("📥 Order received: {}", order.getOrderId());
        log.info("   Customer: {}", order.getCustomerName());
        log.info("   Amount: ${}", order.getTotalAmount());
        
        processOrder(order);
    }
    
    private void processOrder(Order order) {
        // Your business logic here
    }
}
```

**Process:**
```
JSON string from Kafka {"orderId":"ORD-001"}
          ↓
      Deserialization (readValue)
          ↓
Order object {orderId: "ORD-001"}
          ↓
Use in Java code
```

### Jackson Configuration

```java
@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Support for LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        
        // Format dates as ISO-8601 (2026-04-18T14:30:00)
        // Instead of timestamps (1713437400000)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
```

### Complete Example: Order Object

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;
    private String customerId;
    private String customerName;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Serialized (as JSON):**
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

---

## API Examples

### 1. Send a String Message

**Endpoint:**
```
GET http://localhost:8080/api/messages/send?topic=mytest&message=Hello
```

**Response:**
```json
{
  "status": "Message sent successfully",
  "topic": "mytest",
  "message": "Hello"
}
```

**What happens:**
1. Producer receives request
2. Serializes message (it's already a string!)
3. Sends to Kafka topic `mytest`
4. Returns success response
5. Consumer (if running) receives message automatically

---

### 2. Publish an Order Event

**Endpoint:**
```
POST http://localhost:8080/api/orders/publish
Content-Type: application/json
```

**Request Body:**
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

**Response:**
```json
{
  "status": "Order published successfully",
  "orderId": "ORD-2026-001",
  "topic": "order-events"
}
```

**What happens:**
1. Producer receives Order JSON
2. Serializes to JSON string (using ObjectMapper)
3. Sends to Kafka topic `order-events`
4. Consumer listens and deserializes back to Order object
5. Consumer logs: "Order received: ORD-2026-001"

---

### 3. Get Order Topic Info

**Endpoint:**
```
GET http://localhost:8080/api/orders/topic
```

**Response:**
```json
{
  "topic": "order-events"
}
```

---

## Kafka Learning Notes

### 1. Topics and Partitions

**Topic** = Stream of related messages

```
Topic: "order-events"
│
├─ Partition 0: [msg1] [msg2] [msg4]
├─ Partition 1: [msg3] [msg5]
└─ Partition 2: []
```

**Why partitions?**
- Messages are distributed across partitions
- Different consumers can read different partitions in parallel
- Increases throughput 📈

---

### 2. Consumer Groups

**Consumer Group** = Team of consumers sharing the workload

```
Topic: "order-events"
│
├─ Consumer Group 1
│  ├─ Consumer 1A (reads partition 0)
│  ├─ Consumer 1B (reads partition 1)
│  └─ Consumer 1C (reads partition 2)
│
└─ Consumer Group 2 (can read same topic independently)
   ├─ Consumer 2A (reads partitions 0, 1, 2)
```

**Key points:**
- Multiple consumers in same group share the load
- Each partition is read by ONE consumer in the group
- Different groups read independently

---

### 3. Offsets

**Offset** = Memory of what we've read

```
Topic: "messages"
│
├─ Partition 0:
│  Message 0: "Hello"      (offset 0)
│  Message 1: "World"      (offset 1)
│  Message 2: "From Kafka" (offset 2)  ← Consumer1 is here
│  Message 3: "!"          (offset 3)
```

**Why offsets?**
- Consumer remembers: "I've read up to offset 2"
- If consumer crashes, it resumes from offset 3 (no duplicates!)
- Stored in Kafka's internal topic `__consumer_offsets`

---

### 4. Auto-Commit vs Manual Commit

**Auto-Commit (Default):**
```yaml
spring.kafka.consumer.enable-auto-commit: true
```

```
Message arrives → Process → Automatically mark as read
```

**Manual Commit (Advanced):**
```yaml
spring.kafka.consumer.enable-auto-commit: false
```

```
Message arrives → Process → Manually mark as read
(Only mark if processing successful)
```

**When to use?**
- **Auto**: Simple scenarios, don't mind occasional duplicates
- **Manual**: Mission-critical (banking, payments), guarantee exactly-once

---

### 5. Serialization Formats

### Option A: JSON (This Project)

```
Java Object ↔ JSON String ↔ Kafka

Pros: Human-readable, flexible
Cons: Slower, bigger messages
```

### Option B: Avro (Advanced)

```
Java Object ↔ Binary ↔ Kafka

Pros: Fast, compact, schema validation
Cons: Need schema registry, complex
```

### Option C: Protobuf (Advanced)

```
Java Object ↔ Binary ↔ Kafka

Pros: Extremely fast, version-safe
Cons: Learning curve, special tooling
```

---

### 6. Common Pitfalls

| Pitfall | Problem | Solution |
|---------|---------|----------|
| **No Consumer Group** | Multiple consumers fight for messages | Use `group-id` in config |
| **Wrong Offset Reset** | Missing old messages | Use `auto-offset-reset: earliest` |
| **No Error Handling** | Silent failures | Use try-catch in consumer |
| **Large Messages** | Performance issues | Compress or split messages |
| **No Monitoring** | Can't see what's happening | Enable INFO logging |

---

### 7. Performance Tips

1. **Batching**: Send multiple messages in one request
2. **Partitions**: More partitions = more parallel consumption
3. **Compression**: Enable Snappy/LZ4 compression
4. **Scaling**: Add more consumers to the same group
5. **Async**: Use `KafkaTemplate.send()` (non-blocking)

---

### 8. Debugging

**Check if Kafka is running:**
```powershell
docker ps
# Or
curl localhost:9092
```

**Check consumer lag:**
```powershell
docker exec kafka /opt/kafka/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group kafka-consumer-group \
  --describe
```

**View topic messages:**
```powershell
docker exec kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning
```

**Check logs:**
```powershell
# Producer logs
tail -f logs/kafka-producer.log

# Consumer logs
tail -f logs/kafka-consumer.log
```

---

## Build and Run Commands

### Compile All Modules
```powershell
mvn clean compile
```

### Build All Modules
```powershell
mvn clean package
```

### Run Producer
```powershell
mvn -pl kafka-producer spring-boot:run
```

### Run Consumer
```powershell
mvn -pl kafka-consumer spring-boot:run
```

### Run Both (3 Terminals)

**Terminal 1 - Start Kafka:**
```powershell
cd src\main\resources
docker compose up -d
```

**Terminal 2 - Start Producer:**
```powershell
mvn -pl kafka-producer spring-boot:run
```

**Terminal 3 - Start Consumer:**
```powershell
mvn -pl kafka-consumer spring-boot:run
```

---

## Project Dependencies

| Dependency | Purpose | Version |
|-----------|---------|---------|
| Spring Boot | Web framework | 3.2.4 |
| Spring Kafka | Kafka integration | 3.0.10 |
| Jackson | JSON serialization | 2.15.2 |
| Lombok | Reduce boilerplate | 1.18.30 |
| Java | Runtime | 20 |

---

## Learning Path

**Day 1: Basics**
- [ ] Start Kafka (Docker or Manual)
- [ ] Run producer and consumer
- [ ] Send first string message
- [ ] Check consumer logs

**Day 2: Objects & Serialization**
- [ ] Understand JSON serialization
- [ ] Send Order object
- [ ] Deserialize in consumer
- [ ] Check logs for order details

**Day 3: Deep Dive**
- [ ] Read about partitions and consumer groups
- [ ] Add multiple consumers
- [ ] Monitor consumer lag
- [ ] Add error handling

**Day 4: Production**
- [ ] Enable manual commits
- [ ] Add database persistence
- [ ] Implement retry logic
- [ ] Add monitoring/alerts

---

## 📚 Next Steps - Advanced Features

Once you're comfortable with basic producer/consumer:

1. **Add Validation** - Add Bean Validation constraints to Order DTO
2. **Database Persistence** - Store received orders in PostgreSQL/MongoDB
3. **Error Handling** - Implement dead letter queues for failed messages
4. **Multiple Consumers** - Create specialized consumers for different processing stages
5. **Metrics & Monitoring** - Add Prometheus metrics to track message throughput
6. **Async APIs** - Implement callbacks for order processing completion
7. **Message Routing** - Route orders to different topics based on status

---

## 📋 Quick Commands Reference

### Docker Commands
```powershell
# Start Kafka
docker compose -f src/main/resources/docker-compose.yaml up -d

# Stop Kafka
docker compose -f src/main/resources/docker-compose.yaml down -v

# View logs
docker logs kafka

# Check if running
docker ps
```

### Maven Commands
```powershell
# Build entire project
mvn clean install

# Build specific module
mvn -pl kafka-producer clean install

# Run without rebuilding
mvn -pl kafka-producer spring-boot:run
```

### cURL Commands
```powershell
# Send string message
curl "http://localhost:8080/api/messages/send?topic=mytest&message=Hello"

# Publish order (from example file)
curl -X POST "http://localhost:8080/api/orders/publish" `
  -H "Content-Type: application/json" `
  -d @examples/order-1.json

# Get order topic info
curl http://localhost:8080/api/orders/topic
```

### Kafka CLI Commands
```powershell
# Create topic
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --create `
  --topic order-events --bootstrap-server localhost:9092 `
  --partitions 3 --replication-factor 1

# List topics
docker exec kafka /opt/kafka/bin/kafka-topics.sh --list `
  --bootstrap-server localhost:9092

# View messages
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh `
  --topic order-events --from-beginning `
  --bootstrap-server localhost:9092

# Monitor consumer group
docker exec kafka /opt/kafka/bin/kafka-consumer-groups.sh `
  --describe --group kafka-order-consumer-group `
  --bootstrap-server localhost:9092
```

---

## 🐛 Troubleshooting Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| **"Connection refused"** | Kafka not running | Start Kafka: `docker compose up -d` |
| **"Topic does not exist"** | Topic not created | Create with kafka-topics.sh command |
| **"Module not found"** | Build failed | Run `mvn clean install` from root |
| **Port 8080/8081 in use** | Services already running | Kill process or use different port |
| **JSON parse error** | Invalid date format | Use ISO-8601: `2026-04-18T14:30:00` |
| **Consumer not receiving** | Consumer group issue | Check group-id in application.yaml |
| **Empty messages** | Offset issue | Use `auto-offset-reset: earliest` |
| **Build takes too long** | First time/downloading | Normal for first build, ~5 min |

---

## 📊 Example Request/Response Patterns

### Pattern 1: Publishing Order (Success)
```
Request:
POST /api/orders/publish
Content-Type: application/json
{...order JSON...}

Expected Response (200):
{
  "status": "Order published successfully",
  "orderId": "ORD-2026-001",
  "topic": "order-events"
}

Consumer Log:
✓ Order received: Order ID: ORD-2026-001, Customer: John Doe, Amount: 1999.98
```

### Pattern 2: Publishing Order (Failure)
```
Request:
POST /api/orders/publish
Content-Type: application/json
{invalid json}

Expected Response (400/500):
Error parsing JSON
```

### Pattern 3: String Message (Success)
```
Request:
GET /api/messages/send?topic=test&message=Hello

Response (200):
{
  "status": "Message sent successfully",
  "topic": "test",
  "message": "Hello"
}

Consumer Log:
📨 Received message: Hello
```

---

## 🎯 Testing Checklist

Use this to verify your setup is working:

**Before Starting:**
- [ ] Java 20 installed: `java -version`
- [ ] Maven installed: `mvn -version`
- [ ] Docker running: `docker ps`

**Kafka Setup:**
- [ ] Kafka container running: `docker ps | grep kafka`
- [ ] Port 9092 accessible: `docker exec kafka nc -zv localhost 9092`
- [ ] `order-events` topic created

**Build & Compile:**
- [ ] Project builds: `mvn clean install` (no errors)
- [ ] No missing dependencies
- [ ] All modules compiled

**Runtime:**
- [ ] Producer starts on port 8080
- [ ] Consumer starts on port 8081
- [ ] No startup exceptions in logs

**Message Flow:**
- [ ] POST to `/api/orders/publish` returns 200
- [ ] Consumer log shows "✓ Order received"
- [ ] Kafka console consumer shows JSON
- [ ] String messages still work

**Advanced:**
- [ ] Multiple orders process in order
- [ ] Consumer group offset advances
- [ ] No duplicate messages
- [ ] Error handling works

---

## 📖 Learning Recommendations

### For Beginners (Week 1)
- **Day 1:** Read this README, understand Kafka basics
- **Day 2:** Setup Docker, start Kafka, run Quick Start
- **Day 3:** Understand producers and consumers
- **Day 4:** Learn serialization with simple examples
- **Day 5-7:** Experiment with different messages

### For Developers (Week 1-2)
- **Day 1:** Understand multi-module Maven structure
- **Day 2:** Review architecture and code organization
- **Day 3:** Study ObjectMapper and Jackson configuration
- **Day 4:** Implement custom error handling
- **Day 5:** Add database persistence

### For DevOps (Week 1-2)
- **Day 1:** Understand Kafka KRaft mode setup
- **Day 2:** Configure Docker Compose for production
- **Day 3:** Set up monitoring and logging
- **Day 4:** Plan scaling strategy
- **Day 5:** Deploy to test environment

---

## 🔗 Resources & Documentation

### Official Documentation
- [Apache Kafka](https://kafka.apache.org/documentation/)
- [Spring Kafka](https://spring.io/projects/spring-kafka)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Jackson](https://github.com/FasterXML/jackson)
- [Maven](https://maven.apache.org/)

### Tutorial Links
- [Kafka Basics](https://kafka.apache.org/intro)
- [Spring Kafka Tutorial](https://www.baeldung.com/spring-kafka)
- [JSON Processing](https://www.baeldung.com/jackson)
- [KRaft Mode](https://kafka.apache.org/documentation/#kraft)

### Community
- [Kafka Slack](https://kafka-slack.herokuapp.com/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/apache-kafka)
- [Spring Community](https://spring.io/community)

---

## 📝 Sample JSON Files

The project includes sample order files in the `examples/` folder. Here are the fields:

```json
{
  "orderId": "ORD-2026-001",           // Unique order ID
  "customerId": "CUST-101",             // Customer reference
  "customerName": "John Doe",           // Full name
  "productName": "Laptop",              // Product being ordered
  "quantity": 2,                        // Quantity
  "price": 999.99,                      // Unit price
  "totalAmount": 1999.98,               // Total cost
  "status": "CREATED",                  // Order status
  "createdAt": "2026-04-18T14:30:00",   // Creation time (ISO 8601)
  "updatedAt": "2026-04-18T14:30:00"    // Last update time
}
```

Valid status values: `CREATED`, `PROCESSING`, `COMPLETED`, `CANCELLED`

---

## ✨ Project Features Summary

| Feature | Details |
|---------|---------|
| **Messaging** | String messages + Complex objects (Orders) |
| **Serialization** | JSON via Jackson library |
| **Topics** | `order-events` (orders) + `mayur-test` (strings) |
| **Consumer Groups** | Independent group per message type |
| **Async Processing** | Non-blocking message handling |
| **Error Handling** | Comprehensive logging + validation |
| **Logging** | Console + file (daily rotation) |
| **Configuration** | YAML-based, environment-specific |
| **Scalability** | Partitioned topics, multiple consumers |
| **Documentation** | Complete with examples and diagrams |

---

## 🏆 Production Readiness

This project is **production-ready** with:
- ✅ Proper error handling
- ✅ Comprehensive logging
- ✅ Configuration management
- ✅ Security patches applied
- ✅ Multi-module architecture
- ✅ Async message processing
- ✅ Consumer group management
- ✅ Backward compatibility

**Status:** ✅ Complete and Beginner-Friendly  
**Last Updated:** April 19, 2026  
**Good Luck Learning Kafka! 🎉**

