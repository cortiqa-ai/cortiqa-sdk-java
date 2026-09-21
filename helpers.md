# Helpers & Advanced Usage in Cortiqa Java SDK

This guide covers streaming helpers, Spring Boot integration, reactive webflux streaming, custom `HttpClient` configurations, and resilience patterns in `cortiqa-sdk-java`.

---

## 1. Streaming Helpers

### `textStream()`
The `StreamResponse.textStream()` returns a native Java 8+ `java.util.stream.Stream<String>`. It automatically skips null tokens and extracts text chunks from deltas:

```java
try (StreamResponse stream = client.messages().stream(
    ChatCompletionRequest.builder()
        .model("falin-01")
        .addMessage(ChatMessage.user("Explain event loops."))
        .build()
)) {
    // Print each token as it arrives
    stream.textStream().forEach(System.out::print);
}
```

### AutoCloseable Resource Management
`StreamResponse` implements `AutoCloseable`. Always wrap it in a `try-with-resources` block to ensure HTTP network connections and SSE readers are properly closed immediately when consumption completes:

```java
try (StreamResponse stream = client.messages().stream(request)) {
    for (ChatCompletionChunk chunk : stream) {
        // Inspect raw chunk, tool calls, or usage
    }
}
```

---

## 2. Spring Boot Integration

### Dependency Injection with Spring `@Configuration`

```java
package com.example.config;

import co.cortiqa.sdk.CortiqaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CortiqaConfiguration {

    @Bean
    public CortiqaClient cortiqaClient(
        @Value("${cortiqa.api-key}") String apiKey,
        @Value("${cortiqa.base-url:https://api.cortiqa.co}") String baseUrl
    ) {
        return CortiqaClient.builder()
            .apiKey(apiKey)
            .baseURL(baseUrl)
            .timeout(Duration.ofSeconds(45))
            .maxRetries(3)
            .build();
    }
}
```

### Spring WebFlux: Reactive SSE Endpoint
Stream tokens directly to browser/frontend clients as a Server-Sent Events (SSE) `Flux<String>`:

```java
package com.example.controller;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.ChatMessage;
import co.cortiqa.sdk.models.ChatCompletionRequest;
import co.cortiqa.sdk.streaming.StreamResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final CortiqaClient cortiqaClient;

    public ChatController(CortiqaClient cortiqaClient) {
        this.cortiqaClient = cortiqaClient;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String prompt) {
        return Flux.create(sink -> {
            try {
                StreamResponse stream = cortiqaClient.messages().stream(
                    ChatCompletionRequest.builder()
                        .model("falin-01")
                        .addMessage(ChatMessage.user(prompt))
                        .build()
                );

                stream.textStream().forEach(sink::next);
                sink.complete();
                stream.close();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }
}
```

---

## 3. Custom Java 11 `HttpClient` Configuration

You can customize proxy settings, SSL contexts, executor thread pools, or HTTP/2 options:

```java
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

HttpClient customHttpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(15))
    .proxy(ProxySelector.of(new InetSocketAddress("proxy.corp.internal", 8080)))
    .executor(Executors.newFixedThreadPool(16))
    .build();

CortiqaClient client = CortiqaClient.builder()
    .apiKey("sk-cortiqa-...")
    .httpClient(customHttpClient)
    .build();
```

---

## 4. Resilience & Retry Strategy

The SDK includes automatic exponential backoff for:
- HTTP 429 (`RateLimitException`)
- HTTP 500, 502, 503, 504 (`InternalServerException`)
- Network socket drops

You can customize or disable retries:

```java
CortiqaClient client = CortiqaClient.builder()
    .apiKey("sk-cortiqa-...")
    .maxRetries(0) // Disable automatic retries
    .build();
```
