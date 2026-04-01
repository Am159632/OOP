package math;

import core.ItemDistance;
import core.SpaceComponent;
import java.util.ArrayList;
import java.util.List;

public class CentroidFunction<T> implements SpaceFunction<T, T> {
    private String spaceName;
    private List<T> group;

    public CentroidFunction(String spaceName, List<T> group) {
        this.spaceName = spaceName;
        this.group = group;
    }

    @Override
    public T execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        if (group == null || group.isEmpty()) return null;

        double[] first = space.getVector(spaceName, group.get(0));
        if (first == null) return null;

        double[] sumVec = new double[first.length];
        int count = 0;

        for (T id : group) {
            double[] v = space.getVector(spaceName, id);
            if (v != null) {
                for (int i = 0; i < v.length; i++) sumVec[i] += v[i];
                count++;
            }
        }

        if (count == 0) return null;

        for (int i = 0; i < sumVec.length; i++) sumVec[i] /= count;

        List<ItemDistance<T>> closest = SimilaritySearcher.findKClosest(sumVec, new ArrayList<>(), space, strategy, spaceName, 1);

        return closest.isEmpty() ? null : closest.get(0).getId();
    }
}