package co.cortiqa.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelInfo {
    private String id;
    private String name;
    private String description;
    private String provider;
    private Boolean free;

    public ModelInfo() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public Boolean getFree() { return free; }
    public void setFree(Boolean free) { this.free = free; }
}
