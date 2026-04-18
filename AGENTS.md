# AGENTS.md - Kafka Learning Project Guide for AI Agents

## Project Overview

A **multi-module Spring Boot + Kafka learning project** demonstrating producer/consumer patterns with JSON serialization, error handling, and retry mechanisms. **Three modules**: `kafka-common` (shared DTOs), `kafka-producer` (sends messages), `kafka-consumer` (processes messages).

---

## Critical Architecture & Data Flow

### Module Dependencies
```
kafka-producer (port 8080) → [Kafka 9092] ← kafka-consumer (port 8081)
         ↓                                              ↓
    Order.java (serializes)                    Order.java (deserializes)
         ↓                                              ↓
   order-events topic ←──────────────────────────────────────
         ↓
   order-events-retry-0 / order-events-retry-1 (auto-created)
         ↓
   order-events-dlt (Dead Letter Topic, manual review)
```

**Key Pattern**: 
- **Producer**: REST API → Service (ObjectMapper serializes Order to JSON) → KafkaTemplate.send()
- **Consumer**: @KafkaListener receives JSON string → ObjectMapper.readValue() → Business logic
- **DLT**: Message fails after 3 attempts (1 initial + 2 retries with exponential backoff) → @DltHandler

### Configuration Centralization
- **Producer config**: `kafka-producer/src/main/resources/application.yaml` - defines `app.kafka.order-topic: order-events`
- **Consumer config**: `kafka-consumer/src/main/resources/application.yaml` - mirrors same topic, uses `group-id: kafka-consumer-group`
- **JacksonConfig**: Applied in BOTH modules separately; disables `WRITE_DATES_AS_TIMESTAMPS` for ISO-8601 format (`2026-04-18T14:30:00`)

---

## Project-Specific Conventions

### 1. DTO/Entity Design
- **Location**: `kafka-common/src/main/java/com/mm/dto/Order.java`
- **Pattern**: Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`)
- **Serialization Markers**: All fields have `@JsonProperty` and implement `Serializable`
- **Date Handling**: `@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")` for LocalDateTime fields
- **Example field**: `price` and `totalAmount` use `BigDecimal` (not Double) for financial accuracy

### 2. Service Layer (Producers)
- **Package**: `kafka-producer/src/main/java/com/mm/service/`
- **Naming**: `*ProducerService` (e.g., `OrderProducerService`)
- **Pattern**: 
  - Inject `KafkaTemplate<String, String>` and `ObjectMapper`
  - Serialize via `objectMapper.writeValueAsString(order)`
  - Use `.whenComplete()` callback for async result handling (don't block on `send()`)
  - Log partition/offset on success for debugging

### 3. Listener/Consumer Classes
- **Package**: `kafka-consumer/src/main/java/com/mm/listener/`
- **Naming**: `*ConsumerListener` (e.g., `OrderConsumerListener`)
- **Annotation Stack** (from bottom to top):
  ```java
  @RetryableTopic(attempts="3", backoff=@Backoff(delay=1000, multiplier=2.0, maxDelay=10000), ...)
  @KafkaListener(topics="${app.kafka.order-topic:order-events}", groupId="kafka-order-consumer-group")
  public void consumeOrder(String orderJson) { ... }
  ```
- **Error Handling**: Always `throw new RuntimeException()` after logging to trigger retry
- **DLT Handler**: Separate `@DltHandler` method in same class, receives `(String json, Exception exception)`

### 4. REST Controller Patterns
- **Location**: `kafka-producer/src/main/java/com/mm/controller/`
- **Naming**: `*Controller` (e.g., `OrderController`)
- **Response Pattern**: Inner static classes (`OrderResponse`, `ErrorResponse`)
- **Configuration Injection**: `@Value("${app.kafka.order-topic:order-events}")` with defaults

### 5. Logging Configuration
- **Lombok**: Use `@Slf4j` on all Service/Controller/Listener classes
- **Log Levels**: 
  - `com.mm: INFO` (application code)
  - `org.springframework.kafka: INFO` (framework details)
  - `org.apache.kafka: WARN` (suppress verbose client logs)
- **Output**: Console + file rotation to `logs/kafka-{producer|consumer}.log`
- **Format**: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`

---

## Build, Test & Development Workflows

### Build Commands
```bash
# Full multi-module build
mvn clean package                    # Creates JAR in each module's target/

# Single module (producer only)
mvn -pl kafka-producer clean package # Don't build consumer/common

# Compile only (no JAR)
mvn clean compile                    # Test changes quickly
```

### Run Commands (from project root)
```bash
# Terminal 1: Start Kafka (prerequisite)
cd src/main/resources && docker compose up -d    # Runs on localhost:9092

# Terminal 2: Producer
mvn -pl kafka-producer spring-boot:run           # Port 8080, logs to console & logs/kafka-producer.log

# Terminal 3: Consumer
mvn -pl kafka-consumer spring-boot:run           # Port 8081, logs to console & logs/kafka-consumer.log
```

### Testing
- **Test Pattern**: Place tests in `{module}/src/test/java/com/mm/service/` or `listener/`
- **Framework**: JUnit 5 + Spring Test + Kafka Test (embedded broker available via `spring-kafka-test`)
- **Run tests**: `mvn test` or `mvn -pl kafka-producer test`

### Debugging Workflows
```bash
# Check if Kafka is alive
docker ps                                        # Should show kafka container

# View Kafka topics (including auto-created retry/dlt topics)
docker exec kafka /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Monitor consumer group lag
docker exec kafka /opt/kafka/bin/kafka-consumer-groups.sh \
  --describe --group kafka-order-consumer-group --bootstrap-server localhost:9092

# Manually consume from DLT to see failures
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --topic order-events-dlt --from-beginning --bootstrap-server localhost:9092

# Tail application logs
tail -f logs/kafka-producer.log
tail -f logs/kafka-consumer.log
```

---

## Integration Points & External Dependencies

### Kafka Setup (Non-Negotiable)
- **Default Connection**: `localhost:9092` (configured in both `application.yaml` files)
- **Docker Compose**: `src/main/resources/docker-compose.yaml` uses `apache/kafka:4.2.0` with KRaft mode (no ZooKeeper)
- **Topics Auto-Created**: Consumer's `@RetryableTopic` auto-creates `order-events`, `order-events-retry-0`, `order-events-retry-1`, `order-events-dlt`

### Topic Naming Convention
| Topic | Producer | Consumer | Purpose |
|-------|----------|----------|---------|
| `order-events` | OrderProducerService | OrderConsumerListener | Main order events |
| `mayur-test` | MessageController | KafkaConsumerListener | Simple string messages |
| Auto-created retry topics | — | @RetryableTopic | Exponential backoff: 1s → 2s delays |
| `*-dlt` | — | @DltHandler | Manual review queue |

### Spring Boot Version & Dependencies
- **Parent**: `org.springframework.boot:spring-boot-starter-parent:3.2.0`
- **Java**: 20 (compiler + runtime)
- **Key Libraries**:
  - `spring-kafka 3.0.10` (producer/consumer)
  - `jackson-databind 2.15.2` + `jackson-datatype-jsr310` (JSON + LocalDateTime)
  - `lombok 1.18.30` (code generation)
  - `spring-boot-starter-web` (REST controllers)

---

## Common Pitfalls & Solutions

| Scenario | Root Cause | Solution |
|----------|-----------|----------|
| **Consumer doesn't receive messages** | Consumer group never started or topic doesn't exist | Start consumer first; verify with `kafka-topics.sh --list` |
| **JSON parse errors in consumer** | Date format mismatch (timestamp vs ISO-8601) | Ensure `@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")` on Producer Order class AND `mapper.disable(WRITE_DATES_AS_TIMESTAMPS)` in JacksonConfig |
| **"Module not found" build error** | Parent pom.xml in root not processed | Run `mvn clean install` from **project root**, not submodule |
| **Retry topics not created** | `autoCreateTopics="true"` missing from @RetryableTopic | Add it; verify with `kafka-topics.sh --list` after first failure |
| **Port 8080/8081 already in use** | Previous instance still running or other service | `lsof -i :8080` (Mac/Linux) or `netstat -ano \| findstr :8080` (Windows), then kill process |
| **DLT handler never called** | Exception not re-thrown in @KafkaListener | Ensure `throw new RuntimeException()` at end of catch block |

---

## Specific Code Patterns to Reuse

### Producer Service Template
```java
@Slf4j @Service
public class XyzProducerService {
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private ObjectMapper objectMapper;
  
  public void sendXyz(String topic, XyzDto dto) {
    try {
      String json = objectMapper.writeValueAsString(dto);
      kafkaTemplate.send(topic, dto.getId(), json)
        .whenComplete((result, ex) -> {
          if (ex != null) log.error("Failed: {}", ex.getMessage());
          else log.info("Sent to partition: {}, offset: {}", result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        });
    } catch (Exception e) {
      log.error("Serialization failed: {}", e.getMessage(), e);
    }
  }
}
```

### Consumer Listener Template
```java
@Slf4j @Service
public class XyzConsumerListener {
  @Autowired private ObjectMapper objectMapper;
  
  @RetryableTopic(attempts="3", backoff=@Backoff(delay=1000, multiplier=2.0, maxDelay=10000), autoCreateTopics="true", dltTopicSuffix="-dlt", retryTopicSuffix="-retry")
  @KafkaListener(topics="xyz-topic", groupId="xyz-group")
  public void consumeXyz(String json) {
    try {
      XyzDto dto = objectMapper.readValue(json, XyzDto.class);
      // business logic
    } catch (Exception e) {
      log.error("Processing failed (will retry): {}", e.getMessage(), e);
      throw new RuntimeException("Failed", e);
    }
  }
  
  @DltHandler
  public void handleDlt(String json, Exception ex) {
    log.error("DLT: {}, Reason: {}", json, ex.getMessage());
    // save to DB, alert, etc.
  }
}
```

---

## IDE & Editor Integration Notes

- **Build System**: Maven (not Gradle) — use `mvn` commands directly
- **Multi-Module Structure**: IDE should recognize all 3 modules; ensure "Mark Directories As Sources" is done for each `src/main/java` folder
- **Annotation Processing**: Enable annotation processing for Lombok; IntelliJ does this automatically
- **Run Configurations**: Set up separate "Maven Run" configs for producer and consumer with `-pl kafka-producer spring-boot:run` and `-pl kafka-consumer spring-boot:run`

---

## Quick Reference: Key Files

| File | Purpose | Modify When |
|------|---------|------------|
| `pom.xml` (root) | Parent POM, dependency versions | Adding new dependencies |
| `kafka-common/src/.../Order.java` | Shared DTO for all modules | Adding/changing message fields |
| `kafka-producer/.../application.yaml` | Producer config, port 8080, topic name | Changing Kafka host or order topic name |
| `kafka-consumer/.../application.yaml` | Consumer config, port 8081, consumer group | Changing consumer group ID or topic |
| `kafka-producer/.../JacksonConfig.java` | JSON serialization rules | Changing date format or adding custom deserializers |
| `kafka-consumer/.../OrderConsumerListener.java` | Message processing logic, retries, DLT | Adding business logic or changing retry strategy |
| `src/main/resources/docker-compose.yaml` | Kafka startup configuration | Changing Kafka version or adding dependencies |

---

## Environment-Specific Notes

- **Development**: Use Docker Compose for Kafka; logs in `logs/` directory
- **Port Conflicts**: Producer=8080, Consumer=8081, Kafka=9092 (all configurable)
- **Bootstrap Servers**: Hard-coded as `localhost:9092` in `application.yaml` — externalize via environment variable if needed for production

