package examples;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolsExample {
    public static void main(String[] args) {
        CortiqaClient client = CortiqaClient.fromEnv();

        // 1. Define Tool parameter schema
        Map<String, Object> props = new HashMap<>();
        Map<String, Object> accountIdProp = new HashMap<>();
        accountIdProp.put("type", "string");
        accountIdProp.put("description", "Bank Account ID to query");
        props.put("account_id", accountIdProp);

        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        params.put("properties", props);
        params.put("required", List.of("account_id"));

        Tool bankBalanceTool = Tool.function(
            "get_account_balance",
            "Retrieve checking and savings account balances",
            params
        );

        // 2. Call model
        ChatCompletionResponse response = client.messages().create(
            ChatCompletionRequest.builder()
                .model("falin-01")
                .addMessage(ChatMessage.user("What is the current balance in account ACC-9920?"))
                .tools(List.of(bankBalanceTool))
                .build()
        );

        var choice = response.getChoices().get(0);
        if (choice.getMessage().getToolCalls() != null && !choice.getMessage().getToolCalls().isEmpty()) {
            for (ToolCall toolCall : choice.getMessage().getToolCalls()) {
                System.out.println("Model requested Tool Call: " + toolCall.getFunction().getName());
                System.out.println("Arguments: " + toolCall.getFunction().getArguments());
            }
        } else {
            System.out.println(response.getContent());
        }
    }
}
