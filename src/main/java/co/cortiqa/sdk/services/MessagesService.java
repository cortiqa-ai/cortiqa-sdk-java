package co.cortiqa.sdk.services;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.ChatCompletionRequest;
import co.cortiqa.sdk.models.ChatCompletionResponse;
import co.cortiqa.sdk.streaming.StreamResponse;

/**
 * Anthropic-compatible messages service alias.
 */
public class MessagesService {
    private final ChatService.Completions completions;

    public MessagesService(CortiqaClient client) {
        this.completions = new ChatService.Completions(client);
    }

    public ChatCompletionResponse create(ChatCompletionRequest request) {
        return completions.create(request);
    }

    public StreamResponse stream(ChatCompletionRequest request) {
        return completions.stream(request);
    }
}
