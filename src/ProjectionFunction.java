public class ProjectionFunction<T> implements SpaceFunction<T, Double> {
    private String spaceName;
    private T targetId;
    private T startId;
    private T endId;

    public ProjectionFunction(String spaceName, T targetId, T startId, T endId) {
        this.spaceName = spaceName;
        this.targetId = targetId;
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public Double execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        double[] target = space.getVector(spaceName, targetId);
        double[] start = space.getVector(spaceName, startId);
        double[] end = space.getVector(spaceName, endId);

        if (target == null || start == null || end == null) return 0.0;

        double dotProduct = 0;
        double axisMagnitudeSq = 0;

        for (int i = 0; i < target.length; i++) {
            double axisDir = end[i] - start[i];
            dotProduct += (target[i] - start[i]) * axisDir;
            axisMagnitudeSq += axisDir * axisDir;
        }

        if (axisMagnitudeSq == 0) return 0.0;
        return dotProduct / axisMagnitudeSq;
    }
}