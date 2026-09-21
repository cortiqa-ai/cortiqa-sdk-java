# Changelog

All notable changes to `cortiqa-sdk-java` will be documented in this file.

---

## [0.1.0] - 2026-09-21

### Added
- Initial release of the official Cortiqa Java SDK (`co.cortiqa:cortiqa-sdk-java`).
- Support for Java 11, 17, 21+ across Maven and Gradle build systems.
- Dual API interface:
  - Anthropic-style: `client.messages().create` and `client.messages().stream`.
  - OpenAI-style: `client.chat().completions().create` and `client.chat().completions().stream`.
- Support for Falin Foundation Models (`falin-01`, `falin-pro`, `falin-vision`, `falin-ultra`).
- Structured Tool Calling (Function Calling) support.
- Real-time Server-Sent Events (SSE) streaming with Java 8+ `Stream<String>` and `textStream()` helper.
- Automatic exponential backoff retries for 429 rate limits and 5xx server errors.
- Built-in Spring Boot, Android, and Java 11 `HttpClient` compatibility.
- Comprehensive exception hierarchy (`AuthenticationException`, `RateLimitException`, `APIException`).
