package examples;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.ChatCompletionRequest;
import co.cortiqa.sdk.models.ChatMessage;
import co.cortiqa.sdk.streaming.StreamResponse;

public class StreamingExample {
    public static void main(String[] args) throws Exception {
        CortiqaClient client = CortiqaClient.fromEnv();

        System.out.println("Streaming tokens from falin-01:\n");

        try (StreamResponse stream = client.messages().stream(
            ChatCompletionRequest.builder()
                .model("falin-01")
                .addMessage(ChatMessage.user("Write a haiku about clean Java architecture."))
                .build()
        )) {
            // Effortlessly stream text tokens using Java 8 Streams!
            stream.textStream().forEach(System.out::print);
        }

        System.out.println("\n\nStream finished successfully!");
    }
}
