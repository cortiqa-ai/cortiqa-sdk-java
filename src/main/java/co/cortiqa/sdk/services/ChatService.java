package co.cortiqa.sdk.services;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.ChatCompletionRequest;
import co.cortiqa.sdk.models.ChatCompletionResponse;
import co.cortiqa.sdk.streaming.StreamResponse;

public class ChatService {
    private final CortiqaClient client;
    private final Completions completions;

    public ChatService(CortiqaClient client) {
        this.client = client;
        this.completions = new Completions(client);
    }

    public Completions completions() {
        return completions;
    }

    public static class Completions {
        private final CortiqaClient client;

        public Completions(CortiqaClient client) {
            this.client = client;
        }

        public ChatCompletionResponse create(ChatCompletionRequest request) {
            request.setStream(false);
            return client.sendPost("/v1/chat/completions", request, ChatCompletionResponse.class);
        }

        public StreamResponse stream(ChatCompletionRequest request) {
            request.setStream(true);
            return client.sendPostStream("/v1/chat/completions", request);
        }
    }
}
