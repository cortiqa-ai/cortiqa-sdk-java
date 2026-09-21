package co.cortiqa.sdk.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest {
    private String model = "falin-01";
    private List<ChatMessage> messages = new ArrayList<>();
    private Double temperature;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JsonProperty("top_p")
    private Double topP;

    private Boolean stream;
    private List<Tool> tools;

    @JsonProperty("tool_choice")
    private Object toolChoice;

    public ChatCompletionRequest() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ChatCompletionRequest req = new ChatCompletionRequest();

        public Builder model(String model) {
            req.model = model;
            return this;
        }

        public Builder messages(List<ChatMessage> messages) {
            req.messages = messages;
            return this;
        }

        public Builder addMessage(ChatMessage message) {
            req.messages.add(message);
            return this;
        }

        public Builder temperature(Double temperature) {
            req.temperature = temperature;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            req.maxTokens = maxTokens;
            return this;
        }

        public Builder topP(Double topP) {
            req.topP = topP;
            return this;
        }

        public Builder stream(Boolean stream) {
            req.stream = stream;
            return this;
        }

        public Builder tools(List<Tool> tools) {
            req.tools = tools;
            return this;
        }

        public Builder toolChoice(Object toolChoice) {
            req.toolChoice = toolChoice;
            return this;
        }

        public ChatCompletionRequest build() {
            return req;
        }
    }

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }

    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }

    public List<Tool> getTools() { return tools; }
    public void setTools(List<Tool> tools) { this.tools = tools; }

    public Object getToolChoice() { return toolChoice; }
    public void setToolChoice(Object toolChoice) { this.toolChoice = toolChoice; }
}
