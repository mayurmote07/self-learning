# Kafka Learning Project - AI Agent Guide

## Project Overview
This is a Java Maven project for learning Apache Kafka concepts. Currently a minimal setup with standard Maven structure, intended for incremental development of Kafka producers, consumers, and related components.

## Architecture
- **Language**: Java 20
- **Build Tool**: Maven
- **Package Structure**: `com.mm` (single package currently)
- **Entry Point**: `Main.java` with basic hello world loop
- **Resources**: Empty `src/main/resources/` directory (ready for Kafka configs like `application.properties` or `producer.properties`)
- **Tests**: Empty `src/test/java/` directory (follow Maven convention for unit tests)

## Build and Development Workflow
- **Compile**: `mvn compile` (outputs to `target/classes/`)
- **Clean Build**: `mvn clean compile`
- **Run Main**: `mvn exec:java -Dexec.mainClass="com.mm.Main"` or run directly in IDE
- **Package**: `mvn package` (creates JAR in `target/`)
- **Dependencies**: Add Kafka dependencies to `pom.xml` (e.g., `kafka-clients` for core API, or `spring-kafka` for Spring integration)

## Conventions and Patterns
- **Directory Structure**: Standard Maven layout (`src/main/java/`, `src/main/resources/`, `src/test/java/`)
- **Package Naming**: `com.mm` (company/product abbreviation)
- **Versioning**: SNAPSHOT versioning (`1.0-SNAPSHOT`)
- **IDE**: IntelliJ IDEA (`.idea/` directory present, some files gitignored)
- **Git Ignore**: Standard Maven ignores (`target/`, IDE files) plus multi-IDE support

## Key Files
- `pom.xml`: Basic Maven config with Java 20, no dependencies yet
- `src/main/java/com/mm/Main.java`: Current entry point (placeholder code)
- `.gitignore`: Maven-standard ignores with IDE exclusions

## Future Kafka Patterns (When Implemented)
- Expect Kafka configuration files in `src/main/resources/`
- Producer/consumer classes in `com.mm` package
- Test classes mirroring main classes with "Test" suffix
- Properties-driven configuration for brokers, topics, etc.
