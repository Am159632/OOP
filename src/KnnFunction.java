import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnnFunction<T> implements SpaceFunction<T, List<ItemDistance<T>>> {
    private String spaceName;
    private T targetId;
    private int k;

    public KnnFunction(String spaceName, T targetId, int k) {
        this.spaceName = spaceName;
        this.targetId = targetId;
        this.k = k;
    }

    @Override
    public List<ItemDistance<T>> execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        double[] targetVector = space.getVector(spaceName, targetId);
        if (targetVector == null) throw new IllegalArgumentException("Target item not found: " + targetId);

        List<ItemDistance<T>> distances = new ArrayList<>();

        for (T currentId : space.getItems(spaceName)) {
            if (currentId.equals(targetId)) continue;

            double[] currentVec = space.getVector(spaceName, currentId);
            if (currentVec != null) {
                double d = strategy.calculate(targetVector, currentVec);
                distances.add(new ItemDistance<>(currentId, d));
            }
        }

        Collections.sort(distances, new DistanceComparator<T>());

        List<ItemDistance<T>> results = new ArrayList<>();
        for (int i = 0; i < Math.min(k, distances.size()); i++) {
            results.add(distances.get(i));
        }
        return results;
    }
}