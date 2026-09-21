package co.cortiqa.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolCall {
    private String id;
    private String type = "function";
    private FunctionCall function;

    public ToolCall() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public FunctionCall getFunction() { return function; }
    public void setFunction(FunctionCall function) { this.function = function; }
}
