package co.cortiqa.sdk.services;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.ModelInfo;
import co.cortiqa.sdk.models.ModelListResponse;

import java.util.Collections;
import java.util.List;

public class ModelsService {
    private final CortiqaClient client;

    public ModelsService(CortiqaClient client) {
        this.client = client;
    }

    public List<ModelInfo> list() {
        ModelListResponse resp = client.sendGet("/api/v1/ai/models", ModelListResponse.class);
        return resp != null && resp.getData() != null ? resp.getData() : Collections.emptyList();
    }
}
