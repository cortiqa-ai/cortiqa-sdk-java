package examples;

import co.cortiqa.sdk.CortiqaClient;
import co.cortiqa.sdk.models.ModelInfo;

import java.util.List;

public class ModelsExample {
    public static void main(String[] args) {
        CortiqaClient client = CortiqaClient.fromEnv();

        System.out.println("Querying available Cortiqa AI Models...");
        List<ModelInfo> models = client.models().list();

        System.out.println("\nDiscovered " + models.size() + " Models:");
        System.out.println("--------------------------------------------------");
        for (ModelInfo m : models) {
            String tier = Boolean.TRUE.equals(m.getFree()) ? "Free Tier" : "Pro / Enterprise";
            System.out.println("ID:          " + m.getId() + " (" + m.getName() + ")");
            System.out.println("Provider:    " + m.getProvider());
            System.out.println("Tier:        " + tier);
            System.out.println("Description: " + m.getDescription());
            System.out.println("--------------------------------------------------");
        }
    }
}
