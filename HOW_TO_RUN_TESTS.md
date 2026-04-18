# How to Run Tests - Kafka Learning Project

## ✅ 7 Comprehensive Test Classes Created

**Total Test Cases:** 41+

---

## 📁 Test Files Location

```
kafka-learning/
├── kafka-producer/src/test/java/com/mm/
│   ├── service/
│   │   ├── KafkaProducerServiceTest.java      (5 tests)
│   │   └── OrderProducerServiceTest.java      (5 tests)
│   └── controller/
│       ├── MessageControllerTest.java         (6 tests)
│       └── OrderControllerTest.java           (8 tests)
├── kafka-consumer/src/test/java/com/mm/
│   └── listener/
│       ├── KafkaConsumerListenerTest.java     (7 tests)
│       └── OrderConsumerListenerTest.java     (6 tests)
└── kafka-common/src/test/java/com/mm/
    └── config/
        └── JacksonConfigTest.java             (8 tests)
```

---

## 🚀 How to Run Tests

### **Option 1: Using Batch Script (Windows)**

```batch
cd C:\Users\shubh\IdeaProjects\kafka-learning
run-tests.bat
```

This script automatically:
- ✅ Calls Maven with proper path handling
- ✅ Runs `mvn clean test`
- ✅ Shows success/failure summary
- ✅ Works with paths containing spaces

---

### **Option 2: Using PowerShell Script**

```powershell
cd C:\Users\shubh\IdeaProjects\kafka-learning
.\run-tests.ps1
```

Features:
- ✅ Colorized output (Green/Red)
- ✅ Proper path quoting
- ✅ Exit code handling
- ✅ Professional formatting

---

### **Option 3: Using IntelliJ IDE**

**Method 1: Run from IDE**
1. Right-click on test class → Select `Run` or `Run All Tests`
2. Tests run with IntelliJ's built-in test runner
3. View results in Test Explorer

**Method 2: Maven Panel**
1. Open Maven panel (View → Tool Windows → Maven)
2. Expand project → Lifecycle
3. Double-click `test`

---

### **Option 4: From IntelliJ Maven Integration**

The command you provided with proper escaping:

```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" clean test
```

Note: PowerShell requires `&` operator and quotes around paths with spaces.

---

## 📊 Test Summary

### **Producer Tests (kafka-producer)**

| Test Class | Tests | Purpose |
|-----------|-------|---------|
| `KafkaProducerServiceTest` | 5 | String message sending |
| `OrderProducerServiceTest` | 5 | Order serialization & sending |
| `MessageControllerTest` | 6 | REST API `/api/messages/send` |
| `OrderControllerTest` | 8 | REST API `/api/orders/*` |
| **Subtotal** | **24** | **Producer layer** |

### **Consumer Tests (kafka-consumer)**

| Test Class | Tests | Purpose |
|-----------|-------|---------|
| `KafkaConsumerListenerTest` | 7 | String message consumption |
| `OrderConsumerListenerTest` | 6 | Order deserialization |
| **Subtotal** | **13** | **Consumer layer** |

### **Common Tests (kafka-common)**

| Test Class | Tests | Purpose |
|-----------|-------|---------|
| `JacksonConfigTest` | 8 | JSON serialization config |
| **Subtotal** | **8** | **Configuration** |

### **Total: 45+ Test Cases**

---

## 🧪 Test Categories

### **Unit Tests** ✅
- Service layer tests (Mockito)
- Configuration tests
- No Kafka required
- Fast execution (~10 seconds)

### **Controller Tests** ✅
- REST API endpoint tests (MockMvc)
- Parameter validation
- HTTP status codes
- JSON request/response

### **Integration Tests** ⏳
- Component interaction tests
- JSON serialization round-trip
- Error handling scenarios

### **Error Scenarios** ✅
- Invalid JSON
- Missing fields
- Null values
- Empty messages
- Special characters
- Large payloads

---

## 🎯 What Each Test Verifies

### **String Messages**
```
Producer sends → Kafka stores → Consumer receives ✅
```

### **Order Objects**
```
Order object → Serialize to JSON → Kafka → Deserialize → Order object ✅
```

### **REST APIs**
```
HTTP Request → Controller → Service → Response ✅
```

### **Error Handling**
```
Invalid input → Exception handling → Error response ✅
```

---

## ⚙️ Configuration

### **Test Dependencies (Already Included)**
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- AssertJ

### **Maven Plugins Used**
- maven-surefire-plugin (test runner)
- maven-compiler-plugin
- spring-boot-maven-plugin

---

## 📈 Expected Test Results

When you run tests successfully, you should see:

```
[INFO] Tests run: 45, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

---

## 🔍 Running Specific Tests

### **Single Module**
```powershell
# Producer tests only
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" -pl kafka-producer test

# Consumer tests only
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" -pl kafka-consumer test

# Common tests only
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" -pl kafka-common test
```

### **Single Test Class**
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" test -Dtest=KafkaProducerServiceTest
```

### **Single Test Method**
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" test -Dtest=KafkaProducerServiceTest#testSendMessageSuccess
```

---

## 📊 Coverage Reports

Generate code coverage report:

```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" clean test jacoco:report
```

Open report at:
```
target/site/jacoco/index.html
```

---

## ✅ Quick Test Checklist

Before running tests:
- [ ] Java 20 installed
- [ ] Maven configured
- [ ] Git initialized
- [ ] All modules built (`mvn clean install`)
- [ ] No Kafka running required

After tests pass:
- [ ] Check logs: `logs/kafka-producer.log`
- [ ] View coverage report
- [ ] Review test results in IDE
- [ ] Commit test code to git

---

## 🐛 Troubleshooting

### **Problem:** "Maven not found"
**Solution:** Use full path as shown above

### **Problem:** "Tests fail with ClassNotFoundException"
**Solution:** Run `mvn clean install` first to build modules

### **Problem:** "Port already in use"
**Solution:** Tests use MockMvc, no real ports needed. Kill any existing processes.

### **Problem:** "Module not found"
**Solution:** Make sure you're in project root directory

---

## 📝 Test Files Created

1. ✅ `kafka-producer/src/test/java/com/mm/service/KafkaProducerServiceTest.java`
2. ✅ `kafka-producer/src/test/java/com/mm/service/OrderProducerServiceTest.java`
3. ✅ `kafka-producer/src/test/java/com/mm/controller/MessageControllerTest.java`
4. ✅ `kafka-producer/src/test/java/com/mm/controller/OrderControllerTest.java`
5. ✅ `kafka-consumer/src/test/java/com/mm/listener/KafkaConsumerListenerTest.java`
6. ✅ `kafka-consumer/src/test/java/com/mm/listener/OrderConsumerListenerTest.java`
7. ✅ `kafka-common/src/test/java/com/mm/config/JacksonConfigTest.java`

---

## 🎉 You're All Set!

**Test Suite Ready:** ✅  
**Total Test Cases:** 45+  
**Coverage Target:** 80%+  

Run tests using:
```batch
run-tests.bat
```
or
```powershell
.\run-tests.ps1
```

---

**Status:** ✅ Test Suite Complete  
**Last Updated:** April 19, 2026

