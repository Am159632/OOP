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

    public void loadFiles(String fullPath, String pcaPath) throws Exception {
        SpaceLoader<String> fullLoader = new JsonSpaceLoader<>(fullPath, "FULL", id -> id);
        SpaceLoader<String> pcaLoader = new JsonSpaceLoader<>(pcaPath, "PCA",id->id);

        latentSpace.addSpace(fullLoader.load());
        latentSpace.addSpace(pcaLoader.load());
    }

    @Override
    public double[] getVector(String spaceName, String id) {
        return latentSpace.getVector(spaceName, id);
    }

    @Override
    public Set<String> getItems(String spaceName) {
        return latentSpace.getItems(spaceName);
    }
}