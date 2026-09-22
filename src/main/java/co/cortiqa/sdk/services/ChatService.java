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

    public ChatCompletionResponse create(ChatCompletionRequest request) {
        return completions.create(request);
    }

    public StreamResponse stream(ChatCompletionRequest request) {
        return completions.stream(request);
    }

    public static class Completions {
        private final CortiqaClient client;

        public Completions(CortiqaClient client) {
            this.client = client;
        }

        private void normalize(ChatCompletionRequest request) {
            if (request.getModel() == null || request.getModel().trim().isEmpty()) {
                request.setModel(client.getConfig().getDefaultModel());
            }
            if (request.getTools() != null) {
                for (var tool : request.getTools()) {
                    if (tool.getType() == null || tool.getType().isEmpty()) {
                        tool.setType("function");
                    }
                }
            }
        }

        public ChatCompletionResponse create(ChatCompletionRequest request) {
            request.setStream(false);
            normalize(request);
            return client.sendPost("/v1/chat/completions", request, ChatCompletionResponse.class);
        }

        public StreamResponse stream(ChatCompletionRequest request) {
            request.setStream(true);
            normalize(request);
            return client.sendPostStream("/v1/chat/completions", request);
        }
    }
}
