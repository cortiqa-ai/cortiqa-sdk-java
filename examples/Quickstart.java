package examples;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.ChatCompletionRequest;
import co.cortiqa.sdk.models.ChatCompletionResponse;
import co.cortiqa.sdk.models.ChatMessage;

public class Quickstart {
    public static void main(String[] args) {
        // Initialize client from CORTIQA_API_KEY environment variable
        CortiqaClient client = CortiqaClient.fromEnv();

        System.out.println("Sending request to Cortiqa Falin-01...");

        // Option A: Anthropic style (client.messages().create(...))
        ChatCompletionResponse response = client.messages().create(
            ChatCompletionRequest.builder()
                .model("falin-01")
                .addMessage(ChatMessage.system("You are an assistant built by Cortiqa."))
                .addMessage(ChatMessage.user("Explain Spring Boot in two sentences."))
                .maxTokens(300)
                .temperature(0.7)
                .build()
        );

        System.out.println("\nResponse:");
        System.out.println(response.getContent());

        if (response.getUsage() != null) {
            System.out.println("\nTotal tokens used: " + response.getUsage().getTotalTokens());
        }
    }
}
