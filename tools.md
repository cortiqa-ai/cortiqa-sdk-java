# Tool Calling (Function Calling) with Cortiqa Java SDK

Cortiqa foundation models (`falin-01`, `falin-pro`, `falin-ultra`) natively support structured tool calling for invoking Java methods, calling external APIs, querying databases, and building autonomous agent loops.

---

## 1. Defining a Tool

Tools are declared using the `Tool` and `FunctionDefinition` models. Parameter schemas adhere to JSON Schema specifications:

```java
import co.cortiqa.sdk.models.Tool;
import java.util.List;
import java.util.Map;

Tool weatherTool = Tool.function(
    "get_weather",
    "Retrieve current weather and forecast for a given city",
    Map.of(
        "type", "object",
        "properties", Map.of(
            "city", Map.of(
                "type", "string",
                "description", "City name, e.g. San Francisco, Bengaluru, Tokyo"
            ),
            "unit", Map.of(
                "type", "string",
                "enum", List.of("celsius", "fahrenheit")
            )
        ),
        "required", List.of("city")
    )
);
```

---

## 2. Executing an Agentic Tool Loop

Here is a full end-to-end example of providing tools to the model, inspecting model tool call requests, invoking local Java code, and returning the result back to the model:

```java
import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolAgentLoop {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        CortiqaClient client = CortiqaClient.fromEnv();

        // 1. Define the tool
        Tool searchTool = Tool.function(
            "search_database",
            "Search inventory by product SKU or name",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "query", Map.of("type", "string", "description", "Search keyword or SKU")
                ),
                "required", List.of("query")
            )
        );

        // 2. Initial conversation
        List<ChatMessage> conversation = new ArrayList<>();
        conversation.add(ChatMessage.user("Do we have any 'Pro Laptop 16' in stock?"));

        // 3. First model call with tools
        ChatCompletionResponse response = client.messages().create(
            ChatCompletionRequest.builder()
                .model("falin-01")
                .messages(conversation)
                .tools(List.of(searchTool))
                .build()
        );

        ChatCompletionChoice choice = response.getChoices().get(0);
        ChatMessage assistantMsg = choice.getMessage();
        conversation.add(assistantMsg);

        // 4. Check if the model requested any tool calls
        if (assistantMsg.getToolCalls() != null && !assistantMsg.getToolCalls().isEmpty()) {
            for (ToolCall toolCall : assistantMsg.getToolCalls()) {
                String toolName = toolCall.getFunction().getName();
                String argsJson = toolCall.getFunction().getArguments();

                System.out.println("Executing tool: " + toolName + " with args: " + argsJson);

                if ("search_database".equals(toolName)) {
                    Map<String, Object> toolArgs = mapper.readValue(argsJson, Map.class);
                    String query = (String) toolArgs.get("query");

                    // Run Java logic / DB query
                    String toolResult = executeDbSearch(query);

                    // Add tool result to conversation
                    conversation.add(ChatMessage.tool(toolCall.getId(), toolResult));
                }
            }

            // 5. Send back tool results to get the final answer
            ChatCompletionResponse finalResponse = client.messages().create(
                ChatCompletionRequest.builder()
                    .model("falin-01")
                    .messages(conversation)
                    .build()
            );

            System.out.println("\nFinal Assistant Answer:\n" + finalResponse.getContent());
        } else {
            System.out.println(assistantMsg.getContent());
        }
    }

    private static String executeDbSearch(String query) {
        // Mock DB result
        return "{\"item\": \"Pro Laptop 16\", \"in_stock\": 14, \"warehouse\": \"Bangalore-Central\"}";
    }
}
```

---

## 3. Controlling Tool Choice

Use `.toolChoice()` to control how the model uses tools:

- `"auto"` (Default): The model decides whether to respond with text or call a tool.
- `"none"`: Forces the model to never call tools.
- `"required"`: Forces the model to select at least one tool.
- Specific function: Forces the model to call a particular tool.

```java
ChatCompletionRequest request = ChatCompletionRequest.builder()
    .model("falin-01")
    .addMessage(ChatMessage.user("Lookup product 442"))
    .tools(List.of(searchTool))
    .toolChoice("required")
    .build();
```
