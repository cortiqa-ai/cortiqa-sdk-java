package co.cortiqa.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionChoice {
    private int index;
    private ChatMessage message;

    @JsonProperty("finish_reason")
    private String finishReason;

    public ChatCompletionChoice() {}

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public ChatMessage getMessage() { return message; }
    public void setMessage(ChatMessage message) { this.message = message; }

    public String getFinishReason() { return finishReason; }
    public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
}
