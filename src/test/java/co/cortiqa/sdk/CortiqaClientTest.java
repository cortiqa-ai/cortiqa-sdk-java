package co.cortiqa.sdk;

import co.cortiqa.sdk.exception.AuthenticationException;
import co.cortiqa.sdk.models.ChatCompletionRequest;
import co.cortiqa.sdk.models.ChatCompletionResponse;
import co.cortiqa.sdk.models.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CortiqaClientTest {

    @Test
    public void testMissingApiKeyThrowsException() {
        assertThrows(AuthenticationException.class, () -> {
            CortiqaClient.builder().apiKey("").build();
        });
    }

    @Test
    public void testClientBuilderOptions() {
        CortiqaClient client = CortiqaClient.builder()
                .apiKey("sk-cortiqa-test-123")
                .baseURL("https://custom.api.cortiqa.co/")
                .timeout(Duration.ofSeconds(15))
                .maxRetries(3)
                .build();

        assertEquals("sk-cortiqa-test-123", client.getConfig().getApiKey());
        assertEquals("https://custom.api.cortiqa.co", client.getConfig().getBaseURL());
        assertEquals(Duration.ofSeconds(15), client.getConfig().getTimeout());
        assertEquals(3, client.getConfig().getMaxRetries());
    }

    @Test
    public void testSerialization() throws Exception {
        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("falin-01")
                .addMessage(ChatMessage.user("Hello!"))
                .temperature(0.7)
                .maxTokens(200)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(req);

        assertTrue(json.contains("\"model\":\"falin-01\""));
        assertTrue(json.contains("\"content\":\"Hello!\""));
        assertTrue(json.contains("\"temperature\":0.7"));
        assertTrue(json.contains("\"max_tokens\":200"));
    }

    @Test
    public void testResponseDeserialization() throws Exception {
        String jsonResponse = "{\n" +
                "  \"id\": \"chatcmpl-test1234\",\n" +
                "  \"object\": \"chat.completion\",\n" +
                "  \"created\": 1726900000,\n" +
                "  \"model\": \"falin-01\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"Hello from Cortiqa Java SDK!\"\n" +
                "      },\n" +
                "      \"finish_reason\": \"stop\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 10,\n" +
                "    \"completion_tokens\": 6,\n" +
                "    \"total_tokens\": 16\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse response = mapper.readValue(jsonResponse, ChatCompletionResponse.class);

        assertEquals("chatcmpl-test1234", response.getId());
        assertEquals("falin-01", response.getModel());
        assertEquals(1, response.getChoices().size());
        assertEquals("Hello from Cortiqa Java SDK!", response.getContent());
        assertEquals(16, response.getUsage().getTotalTokens());
    }

    @Test
    public void testDefaultModelConfiguration() {
        CortiqaClient defaultClient = CortiqaClient.builder()
                .apiKey("sk-test")
                .build();
        assertEquals("openai/gpt-oss-120b", defaultClient.getConfig().getDefaultModel());

        CortiqaClient customClient = CortiqaClient.builder()
                .apiKey("sk-test")
                .defaultModel("custom/model-v1")
                .build();
        assertEquals("custom/model-v1", customClient.getConfig().getDefaultModel());
    }

    @Test
    public void testReasoningDeserialization() throws Exception {
        String jsonWithReasoning = "{\n" +
                "  \"id\": \"chatcmpl-reasoning-test\",\n" +
                "  \"object\": \"chat.completion\",\n" +
                "  \"created\": 1726900000,\n" +
                "  \"model\": \"openai/gpt-oss-120b\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"Answer is 42\",\n" +
                "        \"reasoning\": \"Step 1: Multiply 6 by 7.\"\n" +
                "      },\n" +
                "      \"finish_reason\": \"stop\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse response = mapper.readValue(jsonWithReasoning, ChatCompletionResponse.class);
        assertEquals("Answer is 42", response.getContent());
        assertEquals("Step 1: Multiply 6 by 7.", response.getReasoning());
        assertEquals("Step 1: Multiply 6 by 7.", response.getChoices().get(0).getMessage().getThought());
    }

    @Test
    public void testBadRequestAndParamError() {
        co.cortiqa.sdk.exception.BadRequestException ex = new co.cortiqa.sdk.exception.BadRequestException(
                "Invalid value", "{}", "temperature", "invalid_param", "invalid_request_error"
        );
        assertEquals(400, ex.getStatusCode());
        assertEquals("temperature", ex.getParam());
        assertEquals("invalid_param", ex.getCode());
        assertTrue(ex.getMessage().contains("temperature"));
    }
}
