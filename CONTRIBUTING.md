# Contributing to Cortiqa Java SDK

We welcome contributions to `co.cortiqa:cortiqa-sdk-java`!

---

## Prerequisites

- **Java Development Kit (JDK)**: Java 11 or newer (Java 17 / 21 recommended)
- **Build Tool**: Maven 3.8+ or Gradle 7+

---

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/cortiqa-ai/cortiqa-sdk-java.git
   cd cortiqa-sdk-java
   ```

2. **Build with Maven:**
   ```bash
   mvn clean compile
   ```

3. **Run Unit Tests:**
   ```bash
   mvn test
   ```

4. **Or build with Gradle:**
   ```bash
   ./gradlew test
   ```

---

## Code Quality Standards

- Maintain Java 11 bytecode compatibility.
- Ensure all public APIs have clear JavaDoc comments.
- Keep external runtime dependencies minimal (Jackson is the primary dependency for JSON serialization).
- All new features should be accompanied by JUnit tests in `src/test/java`.
