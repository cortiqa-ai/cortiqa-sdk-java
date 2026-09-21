package co.cortiqa.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelListResponse {
    private boolean success;
    private List<ModelInfo> data;

    public ModelListResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<ModelInfo> getData() { return data; }
    public void setData(List<ModelInfo> data) { this.data = data; }
}
