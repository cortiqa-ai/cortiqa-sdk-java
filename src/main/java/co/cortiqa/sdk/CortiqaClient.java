package co.cortiqa.sdk;

import co.cortiqa.sdk.exception.*;
import co.cortiqa.sdk.services.ChatService;
import co.cortiqa.sdk.services.MessagesService;
import co.cortiqa.sdk.services.ModelsService;
import co.cortiqa.sdk.streaming.StreamResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Main client entry point for Cortiqa AI.
 *
 * <pre>{@code
 * CortiqaClient client = CortiqaClient.builder()
 *     .apiKey("sk-cortiqa-...")
 *     .build();
 *
 * ChatCompletionResponse response = client.messages().create(
 *     ChatCompletionRequest.builder()
 *         .model("falin-01")
 *         .addMessage(ChatMessage.user("Hello Cortiqa!"))
 *         .build()
 * );
 * System.out.println(response.getContent());
 * }</pre>
 */
public class CortiqaClient {
    private final CortiqaConfig config;
    private final ObjectMapper mapper;
    private final ChatService chat;
    private final MessagesService messages;
    private final ModelsService models;

    public CortiqaClient(CortiqaConfig config) {
        this.config = config;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.chat = new ChatService(this);
        this.messages = new MessagesService(this);
        this.models = new ModelsService(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static CortiqaClient fromEnv() {
        return builder().build();
    }

    public ChatService chat() { return chat; }
    public MessagesService messages() { return messages; }
    public ModelsService models() { return models; }
    public CortiqaConfig getConfig() { return config; }

    public <T> T sendGet(String path, Class<T> responseClass) {
        return executeWithRetry(path, "GET", null, responseClass);
    }

    public <T> T sendPost(String path, Object body, Class<T> responseClass) {
        return executeWithRetry(path, "POST", body, responseClass);
    }

    public StreamResponse sendPostStream(String path, Object body) {
        try {
            String jsonBody = mapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseURL() + path))
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "cortiqa-java/" + CortiqaConfig.VERSION)
                    .timeout(config.getTimeout())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<InputStream> response = config.getHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String errText = new String(response.body().readAllBytes());
                handleError(response.statusCode(), errText);
            }

            return new StreamResponse(response.body(), mapper);
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new CortiqaException("Failed to open Cortiqa SSE stream: " + e.getMessage(), e);
        }
    }

    private <T> T executeWithRetry(String path, String method, Object body, Class<T> responseClass) {
        int attempt = 0;
        String jsonBody = null;

        try {
            if (body != null) {
                jsonBody = mapper.writeValueAsString(body);
            }
        } catch (Exception e) {
            throw new CortiqaException("Failed to serialize request payload: " + e.getMessage(), e);
        }

        while (true) {
            try {
                HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(config.getBaseURL() + path))
                        .header("Authorization", "Bearer " + config.getApiKey())
                        .header("Content-Type", "application/json")
                        .header("User-Agent", "cortiqa-java/" + CortiqaConfig.VERSION)
                        .timeout(config.getTimeout());

                if ("POST".equalsIgnoreCase(method)) {
                    reqBuilder.POST(jsonBody != null ? HttpRequest.BodyPublishers.ofString(jsonBody) : HttpRequest.BodyPublishers.noBody());
                } else {
                    reqBuilder.GET();
                }

                HttpResponse<String> response = config.getHttpClient().send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());

                int statusCode = response.statusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    return mapper.readValue(response.body(), responseClass);
                }

                // Handle retries on 429 or 5xx
                if ((statusCode == 429 || statusCode >= 500) && attempt < config.getMaxRetries()) {
                    attempt++;
                    long backoffMs = (long) (Math.pow(2, attempt) * 500);
                    Thread.sleep(backoffMs);
                    continue;
                }

                handleError(statusCode, response.body());
            } catch (APIException e) {
                throw e;
            } catch (Exception e) {
                if (attempt < config.getMaxRetries()) {
                    attempt++;
                    try {
                        Thread.sleep((long) (Math.pow(2, attempt) * 500));
                        continue;
                    } catch (InterruptedException ignored) {}
                }
                throw new CortiqaException("Cortiqa connection failed: " + e.getMessage(), e);
            }
        }
    }

    /**
     * One-liner convenience helper to prompt a model and get string response directly.
     */
    public String prompt(String prompt) {
        return prompt(prompt, null);
    }

    public String prompt(String prompt, String system) {
        co.cortiqa.sdk.models.ChatCompletionRequest.Builder builder = co.cortiqa.sdk.models.ChatCompletionRequest.builder();
        if (system != null && !system.isEmpty()) {
            builder.addMessage(co.cortiqa.sdk.models.ChatMessage.system(system));
        }
        builder.addMessage(co.cortiqa.sdk.models.ChatMessage.user(prompt));
        var response = chat.create(builder.build());
        return response.getContent();
    }

    private void handleError(int statusCode, String body) {
        String msg = body;
        String param = null;
        String code = null;
        String errorType = null;

        try {
            var node = mapper.readTree(body);
            if (node.has("detail")) {
                var detailNode = node.get("detail");
                if (detailNode.isArray() && detailNode.size() > 0) {
                    var first = detailNode.get(0);
                    if (first.has("loc") && first.get("loc").isArray() && first.get("loc").size() > 0) {
                        var locArr = first.get("loc");
                        param = locArr.get(locArr.size() - 1).asText();
                    }
                    if (first.has("type")) code = first.get("type").asText();
                    if (first.has("msg")) {
                        msg = param != null ? "Parameter '" + param + "': " + first.get("msg").asText() : first.get("msg").asText();
                    }
                } else if (detailNode.isTextual()) {
                    msg = detailNode.asText();
                }
            } else if (node.has("error")) {
                var errNode = node.get("error");
                if (errNode.isTextual()) {
                    msg = errNode.asText();
                } else if (errNode.isObject()) {
                    if (errNode.has("message")) msg = errNode.get("message").asText();
                    if (errNode.has("param")) param = errNode.get("param").asText();
                    if (errNode.has("code")) code = errNode.get("code").asText();
                    if (errNode.has("type")) errorType = errNode.get("type").asText();
                }
            } else if (node.has("message")) {
                msg = node.get("message").asText();
            }
        } catch (Exception ignored) {}

        switch (statusCode) {
            case 400:
                throw new BadRequestException(msg, body, param, code, errorType);
            case 401:
                throw new AuthenticationException(statusCode, msg, body);
            case 403:
                throw new PermissionDeniedException(statusCode, msg, body);
            case 404:
                throw new NotFoundException(statusCode, msg, body);
            case 422:
                throw new UnprocessableEntityException(msg, body, param, code, errorType);
            case 429:
                throw new RateLimitException(statusCode, msg, body);
            default:
                if (statusCode >= 500) {
                    throw new InternalServerException(statusCode, msg, body);
                }
                throw new APIException(statusCode, msg, body, param, code, errorType);
        }
    }

    public static class Builder {
        private String apiKey;
        private String baseURL;
        private String defaultModel = CortiqaConfig.DEFAULT_MODEL;
        private Duration timeout;
        private int maxRetries = CortiqaConfig.DEFAULT_MAX_RETRIES;
        private HttpClient httpClient;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder baseURL(String baseURL) {
            this.baseURL = baseURL;
            return this;
        }

        public Builder defaultModel(String defaultModel) {
            this.defaultModel = defaultModel;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public CortiqaClient build() {
            String key = this.apiKey != null ? this.apiKey : System.getenv("CORTIQA_API_KEY");
            if (key == null || key.trim().isEmpty()) {
                throw new AuthenticationException(401, "No API key provided. Pass `apiKey` or set CORTIQA_API_KEY environment variable.", null);
            }
            String url = this.baseURL != null ? this.baseURL : System.getenv("CORTIQA_BASE_URL");
            CortiqaConfig config = new CortiqaConfig(key, url, this.defaultModel, this.timeout, this.maxRetries, this.httpClient);
            return new CortiqaClient(config);
        }
    }
}
