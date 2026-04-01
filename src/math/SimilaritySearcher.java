package math;

import core.DistanceComparator;
import core.ItemDistance;
import core.SpaceComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimilaritySearcher {

    public static <T> List<ItemDistance<T>> findKClosest(
            double[] targetVector,
            List<T> itemsToSkip,
            SpaceComponent<T> space,
            DistanceStrategy strategy,
            String spaceName,
            int k) {

        if (targetVector == null) return new ArrayList<>();

        List<ItemDistance<T>> distances = new ArrayList<>();

        for (T currentId : space.getItems(spaceName)) {
            if (itemsToSkip != null && itemsToSkip.contains(currentId)) continue;

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