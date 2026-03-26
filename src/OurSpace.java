import java.io.File;
import java.util.Set;

public class OurSpace extends AbstractAnalyzableSpace<String> {

    private CompositeSpace<String> latentSpace;

    public OurSpace() {
        this.latentSpace = new CompositeSpace<>("Word Embedding System");
    }

    @Override
    protected SpaceComponent<String> getDataSpace() {
        return this.latentSpace;
    }

    private void runPythonScript(String scriptName) {
        try {
            System.out.println("Starting Python script: " + scriptName + "...");

            ProcessBuilder pb = new ProcessBuilder("python", scriptName);
            Process p = pb.start();
            p.waitFor();

            System.out.println("Python script finished successfully!");
        } catch (Exception e) {
            System.err.println("Error running python script: " + e.getMessage());
        }
    }

    public void loadFiles(String fullPath, String pcaPath) throws Exception {

        File fullFile = new File(fullPath);
        File pcaFile = new File(pcaPath);
        if (!fullFile.exists() || !pcaFile.exists()) {
            System.out.println("JSON files not found. Starting Python script to generate them...");
            runPythonScript("embedder.py");
        } else {
            System.out.println("JSON files already exist! Skipping Python generation. Loading directly...");
        }
        SpaceLoader<String> fullLoader = new JsonSpaceLoader<>(fullPath, "FULL", id -> id);
        SpaceLoader<String> pcaLoader = new JsonSpaceLoader<>(pcaPath, "PCA", id -> id);

        latentSpace.addSpace(fullLoader.load());
        latentSpace.addSpace(pcaLoader.load());
    }
}