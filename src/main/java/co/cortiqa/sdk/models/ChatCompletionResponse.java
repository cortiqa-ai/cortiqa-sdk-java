package co.cortiqa.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChatCompletionChoice> choices;
    private Usage usage;

    public ChatCompletionResponse() {}

    /**
     * Helper to retrieve the first choice message content directly (Anthropic style).
     */
    public String getContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }

    public long getCreated() { return created; }
    public void setCreated(long created) { this.created = created; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<ChatCompletionChoice> getChoices() { return choices; }
    public void setChoices(List<ChatCompletionChoice> choices) { this.choices = choices; }

    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }
}
