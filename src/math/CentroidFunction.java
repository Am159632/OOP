package math;

import core.ItemDistance;
import core.SpaceComponent;
import java.util.ArrayList;
import java.util.List;

public class CentroidFunction<T> implements SpaceFunction<T, List<ItemDistance<T>>> {
    private String spaceName;
    private List<T> group;
    private int k;

    public CentroidFunction(String spaceName, List<T> group, int k) {
        this.spaceName = spaceName;
        this.group = group;
        this.k = k;
    }

    @Override
    public List<ItemDistance<T>> execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        if (group == null || group.isEmpty()) return new ArrayList<>();

        double[] first = space.getVector(spaceName, group.get(0));
        if (first == null) return new ArrayList<>();

        double[] sumVec = new double[first.length];
        int count = 0;

        for (T id : group) {
            double[] v = space.getVector(spaceName, id);
            if (v != null) {
                for (int i = 0; i < v.length; i++) sumVec[i] += v[i];
                count++;
            }
        }

        if (count == 0) return new ArrayList<>();

        for (int i = 0; i < sumVec.length; i++) sumVec[i] /= count;

        return SimilaritySearcher.findKClosest(sumVec, new ArrayList<>(), space, strategy, spaceName, k);
    }
}