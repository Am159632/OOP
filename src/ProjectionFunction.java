import java.util.ArrayList;
import java.util.List;

public class ProjectionFunction<T> implements SpaceFunction<T, List<ItemDistance<T>>> {
    private String spaceName;
    private T startId;
    private T endId;

    public ProjectionFunction(String spaceName, T startId, T endId) {
        this.spaceName = spaceName;
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public List<ItemDistance<T>> execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        double[] start = space.getVector(spaceName, startId);
        double[] end = space.getVector(spaceName, endId);
        if (start == null || end == null) return new ArrayList<>();

        List<ItemDistance<T>> projections = new ArrayList<>();

        for (T item : space.getItems(spaceName)) {
            if (item.equals(startId) || item.equals(endId)) continue;

            double[] target = space.getVector(spaceName, item);
            if (target == null) continue;

            double dotProduct = 0;
            double axisMagnitudeSq = 0;

            for (int i = 0; i < target.length; i++) {
                double axisDir = end[i] - start[i];
                dotProduct += (target[i] - start[i]) * axisDir;
                axisMagnitudeSq += axisDir * axisDir;
            }

            double val = (axisMagnitudeSq == 0) ? 0.0 : dotProduct / axisMagnitudeSq;
            projections.add(new ItemDistance<>(item, val));
        }
        return projections;
    }
}