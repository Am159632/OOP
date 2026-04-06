package extra;

import core.SpaceComponent;
import math.DistanceStrategy;
import math.SpaceFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomFunction<T> implements SpaceFunction<T, T> {
    private String spaceName;
    private List<Term<T>> terms;

    public CustomFunction(String spaceName, List<Term<T>> terms) {
        this.spaceName = spaceName;
        this.terms = terms;
    }

    @Override
    public T execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        double[] resultVec = null;
        Set<T> excluded = new HashSet<>();

        for (Term<T> term : terms) {
            double[] vec = space.getVector(spaceName, term.item);
            excluded.add(term.item);

            if (vec == null) continue;

            if (resultVec == null) {
                resultVec = new double[vec.length];
            }

            for (int i = 0; i < vec.length; i++) {
                resultVec[i] += term.isAdd ? vec[i] : -vec[i];
            }
        }

        if (resultVec == null) return null;

        T bestItem = null;
        double bestDist = Double.MAX_VALUE;

        for (T item : space.getItems(spaceName)) {
            if (excluded.contains(item)) continue;

            double[] currentVec = space.getVector(spaceName, item);
            if (currentVec != null) {
                double dist = strategy.calculate(resultVec, currentVec);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestItem = item;
                }
            }
        }
        return bestItem;
    }
}