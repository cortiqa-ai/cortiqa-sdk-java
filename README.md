# Cortiqa AI Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/co.cortiqa/cortiqa-sdk-java.svg)](https://central.sonatype.com/artifact/co.cortiqa/cortiqa-sdk-java)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 11+](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://adoptium.net/)

Official Java client library for **Cortiqa AI** and **Falin Foundation Models**. Built for enterprise applications, Spring Boot backends, Android apps, and high-concurrency microservices.

---

## ⚡ Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>co.cortiqa</groupId>
    <artifactId>cortiqa-sdk-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

In your `build.gradle`:

```groovy
implementation 'co.cortiqa:cortiqa-sdk-java:0.1.0'
```

---

## 🚀 Quickstart

Set your API key:

```bash
export CORTIQA_API_KEY="sk-cortiqa-your-api-key"
```

Then create a chat completion:

```java
import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.*;

public class Main {
    public static void main(String[] args) {
        // Reads from CORTIQA_API_KEY environment variable
        CortiqaClient client = CortiqaClient.fromEnv();

        ChatCompletionResponse response = client.messages().create(
            ChatCompletionRequest.builder()
                .model("falin-01")
                .addMessage(ChatMessage.user("Explain reactive microservices in 2 sentences."))
                .build()
        );

        System.out.println(response.getContent());
    }
}
```

---

## 🌊 Real-Time Token Streaming

Stream tokens using Java 8+ Streams:

```java
import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.*;
import co.cortiqa.sdk.streaming.StreamResponse;

public class StreamingDemo {
    public static void main(String[] args) throws Exception {
        CortiqaClient client = CortiqaClient.fromEnv();

        try (StreamResponse stream = client.messages().stream(
            ChatCompletionRequest.builder()
                .model("falin-01")
                .addMessage(ChatMessage.user("Write a poem about Mumbai rains."))
                .build()
        )) {
            // Stream tokens directly to stdout
            stream.textStream().forEach(System.out::print);
        }
    }
}
```

---

## 🛠️ Tool Calling (Function Calling)

```java
import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.*;
import java.util.List;
import java.util.Map;

public class ToolDemo {
    public static void main(String[] args) {
        CortiqaClient client = CortiqaClient.fromEnv();

        Tool weatherTool = Tool.function(
            "get_weather",
            "Get temperature for a city",
            Map.of("city", Map.of("type", "string"))
        );

        ChatCompletionResponse response = client.messages().create(
            ChatCompletionRequest.builder()
                .model("falin-01")
                .addMessage(ChatMessage.user("What is the weather in Delhi?"))
                .tools(List.of(weatherTool))
                .build()
        );

        var toolCalls = response.getChoices().get(0).getMessage().getToolCalls();
        if (toolCalls != null && !toolCalls.isEmpty()) {
            System.out.println("Model requested tool: " + toolCalls.get(0).getFunction().getName());
        }
    }
}
```

---

## 🍃 Spring Boot Integration

Inject `CortiqaClient` as a Spring Bean:

```java
@Configuration
public class CortiqaConfig {

    @Bean
    public CortiqaClient cortiqaClient(@Value("${cortiqa.api.key}") String apiKey) {
        return CortiqaClient.builder()
                .apiKey(apiKey)
                .timeout(Duration.ofSeconds(30))
                .maxRetries(3)
                .build();
    }
}
```

---

## ⚙️ Custom Configuration

```java
CortiqaClient client = CortiqaClient.builder()
    .apiKey("sk-cortiqa-...")
    .baseURL("https://api.cortiqa.co")
    .timeout(Duration.ofSeconds(45))
    .maxRetries(3)
    .httpClient(customJavaHttpClient)
    .build();
```

---

## 🛡️ Exception Handling

```java
try {
    var response = client.messages().create(request);
} catch (AuthenticationException e) {
    System.err.println("Invalid API key!");
} catch (RateLimitException e) {
    System.err.println("Rate limit reached: " + e.getMessage());
} catch (APIException e) {
    System.err.println("API error " + e.getStatusCode() + ": " + e.getMessage());
}
```

---

## 📄 License

MIT © [Cortiqa AI](https://cortiqa.co)
