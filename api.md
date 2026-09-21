# Cortiqa Java SDK API Reference

Comprehensive reference for all packages, classes, methods, models, and exceptions in `co.cortiqa.sdk`.

---

## Table of Contents

- [Client Initialization](#client-initialization)
- [Services](#services)
  - [`client.messages()`](#clientmessages)
  - [`client.chat().completions()`](#clientchatcompletions)
  - [`client.models()`](#clientmodels)
- [Data Models](#data-models)
  - [`ChatCompletionRequest`](#chatcompletionrequest)
  - [`ChatCompletionResponse`](#chatcompletionresponse)
  - [`ChatMessage`](#chatmessage)
  - [`Tool` & `FunctionDefinition`](#tool--functiondefinition)
  - [`StreamResponse`](#streamresponse)
- [Exception Hierarchy](#exception-hierarchy)

---

## Client Initialization

The primary entry point is `co.cortiqa.sdk.CortiqaClient`.

### Default / Environment Initialization
```java
// Reads CORTIQA_API_KEY from environment variables
CortiqaClient client = CortiqaClient.fromEnv();
```

### Direct Key Initialization
```java
CortiqaClient client = CortiqaClient.withApiKey("sk-cortiqa-your-key");
```

### Fluent Builder Configuration
```java
CortiqaClient client = CortiqaClient.builder()
    .apiKey("sk-cortiqa-your-key")
    .baseURL("https://api.cortiqa.co")    // Optional custom backend
    .timeout(Duration.ofSeconds(60))       // Request timeout
    .maxRetries(3)                         // Exponential backoff attempts
    .httpClient(customHttpClient)          // Optional custom java.net.http.HttpClient
    .build();
```

---

## Services

### `client.messages()`

Provides an Anthropic-style interface for message generation and streaming.

#### `client.messages().create(ChatCompletionRequest request)`
Sends a synchronous completion request and returns `ChatCompletionResponse`.

```java
ChatCompletionResponse response = client.messages().create(
    ChatCompletionRequest.builder()
        .model("falin-01")
        .addMessage(ChatMessage.user("Explain quantum computing."))
        .temperature(0.7)
        .maxTokens(1024)
        .build()
);

System.out.println(response.getContent());
```

#### `client.messages().stream(ChatCompletionRequest request)`
Initiates a real-time Server-Sent Events (SSE) stream and returns an `AutoCloseable` `StreamResponse`.

```java
try (StreamResponse stream = client.messages().stream(request)) {
    stream.textStream().forEach(System.out::print);
}
```

---

### `client.chat().completions()`

Provides an OpenAI-compatible interface. Has the exact same signature and behavior as `client.messages()`.

```java
ChatCompletionResponse response = client.chat().completions().create(request);
StreamResponse stream = client.chat().completions().stream(request);
```

---

### `client.models()`

List available Cortiqa foundation models and view their metadata.

#### `client.models().list()`
```java
ModelListResponse models = client.models().list();
for (ModelInfo model : models.getData()) {
    System.out.println(model.getId() + " - " + model.getDescription());
}
```

---

## Data Models

All models reside under package `co.cortiqa.sdk.models`.

### `ChatCompletionRequest`
Constructed via fluent Builder:

| Method | Type | Description |
|---|---|---|
| `model(String)` | `String` | Model ID (`falin-01`, `falin-pro`, `falin-vision`, `falin-ultra`) |
| `messages(List<ChatMessage>)` | `List` | Complete list of conversation messages |
| `addMessage(ChatMessage)` | `ChatMessage` | Appends a single message to the conversation |
| `temperature(Double)` | `Double` | Sampling temperature (0.0 to 2.0) |
| `maxTokens(Integer)` | `Integer` | Maximum completion tokens |
| `topP(Double)` | `Double` | Nucleus sampling probability |
| `tools(List<Tool>)` | `List` | Tool/function definitions available to the model |
| `toolChoice(Object)` | `String/Object` | `"auto"`, `"none"`, `"required"`, or specific function |

### `ChatMessage`
Factory helpers for constructing conversation turns:

```java
ChatMessage.system("You are a helpful coding assistant.");
ChatMessage.user("How do I sort a list in Java?");
ChatMessage.assistant("Use Collections.sort(list) or list.sort(Comparator.naturalOrder()).");
ChatMessage.tool("call_123", "{\"status\": \"success\"}");
```

### `StreamResponse`
Package `co.cortiqa.sdk.streaming.StreamResponse`. Implements `AutoCloseable` and `Iterable<ChatCompletionChunk>`:

```java
StreamResponse stream = client.messages().stream(request);

// 1. Text token stream (Java 8+ Stream<String>)
stream.textStream().forEach(System.out::print);

// 2. Full chunk stream (Java 8+ Stream<ChatCompletionChunk>)
stream.stream().forEach(chunk -> {
    // Process deltas, tool calls, finish reasons
});
```

---

## Exception Hierarchy

All exceptions extend `co.cortiqa.sdk.exception.CortiqaException` (unchecked `RuntimeException`):

```
CortiqaException
└── APIException
    ├── AuthenticationException (HTTP 401)
    ├── RateLimitException      (HTTP 429)
    ├── NotFoundException       (HTTP 404)
    └── InternalServerException (HTTP 500, 502, 503, 504)
```

Example:

```java
try {
    ChatCompletionResponse response = client.messages().create(request);
} catch (AuthenticationException e) {
    System.err.println("Check your CORTIQA_API_KEY: " + e.getMessage());
} catch (RateLimitException e) {
    System.err.println("Rate limit reached. Retry after delay: " + e.getMessage());
} catch (APIException e) {
    System.err.println("HTTP " + e.getStatusCode() + ": " + e.getResponseBody());
} catch (CortiqaException e) {
    System.err.println("SDK client error: " + e.getMessage());
}
```
