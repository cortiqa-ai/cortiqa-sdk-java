package co.cortiqa.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionChunk {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChunkChoice> choices;

    public static class ChunkChoice {
        private int index;
        private ChunkDelta delta;

        @JsonProperty("finish_reason")
        private String finishReason;

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }

        public ChunkDelta getDelta() { return delta; }
        public void setDelta(ChunkDelta delta) { this.delta = delta; }

        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }

    public static class ChunkDelta {
        private String role;
        private String content;

        @JsonProperty("reasoning")
        private String reasoning;

        @JsonProperty("reasoning_content")
        private String reasoningContent;

        @JsonProperty("tool_calls")
        private List<ToolCall> toolCalls;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getReasoning() { return reasoning; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }

        public String getReasoningContent() { return reasoningContent; }
        public void setReasoningContent(String reasoningContent) { this.reasoningContent = reasoningContent; }

        public List<ToolCall> getToolCalls() { return toolCalls; }
        public void setToolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }

    public long getCreated() { return created; }
    public void setCreated(long created) { this.created = created; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<ChunkChoice> getChoices() { return choices; }
    public void setChoices(List<ChunkChoice> choices) { this.choices = choices; }
}
