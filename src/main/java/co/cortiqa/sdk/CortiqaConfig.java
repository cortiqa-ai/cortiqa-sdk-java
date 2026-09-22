package co.cortiqa.sdk;

import java.net.http.HttpClient;
import java.time.Duration;

public class CortiqaConfig {
    public static final String DEFAULT_BASE_URL = "https://api.cortiqa.co";
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    public static final int DEFAULT_MAX_RETRIES = 2;
    public static final String DEFAULT_MODEL = "openai/gpt-oss-120b";
    public static final String VERSION = "0.1.1";

    private final String apiKey;
    private final String baseURL;
    private final String defaultModel;
    private final Duration timeout;
    private final int maxRetries;
    private final HttpClient httpClient;

    public CortiqaConfig(String apiKey, String baseURL, Duration timeout, int maxRetries, HttpClient httpClient) {
        this(apiKey, baseURL, DEFAULT_MODEL, timeout, maxRetries, httpClient);
    }

    public CortiqaConfig(String apiKey, String baseURL, String defaultModel, Duration timeout, int maxRetries, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.baseURL = baseURL != null ? baseURL.replaceAll("/+$", "") : DEFAULT_BASE_URL;
        this.defaultModel = defaultModel != null && !defaultModel.isEmpty() ? defaultModel : DEFAULT_MODEL;
        this.timeout = timeout != null ? timeout : DEFAULT_TIMEOUT;
        this.maxRetries = maxRetries >= 0 ? maxRetries : DEFAULT_MAX_RETRIES;
        this.httpClient = httpClient != null ? httpClient : HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
    }

    public String getApiKey() { return apiKey; }
    public String getBaseURL() { return baseURL; }
    public String getDefaultModel() { return defaultModel; }
    public Duration getTimeout() { return timeout; }
    public int getMaxRetries() { return maxRetries; }
    public HttpClient getHttpClient() { return httpClient; }
}
