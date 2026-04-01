package math;

import core.ItemDistance;
import core.SpaceComponent;
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
        if (targetVector == null) throw new IllegalArgumentException("Target item not found");

        return SimilaritySearcher.findKClosest(targetVector, List.of(targetId), space, strategy, spaceName, k);
    }
}