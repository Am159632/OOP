package actions;

import core.*;
import math.*;
import visuals.SpaceVisualizer;

import java.util.List;
import java.util.stream.Collectors;

public class CentroidAction<T> extends AbstractAnalysisAction<T> {
    private List<T> group;
    private List<ItemDistance<T>> result;
    private int k;

    public CentroidAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, List<T> group, int k) {
        super(space, visualizer, strategy);
        this.group = group;
        this.k = k;
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            SpaceFunction<T, List<ItemDistance<T>>> centroidFunc = new CentroidFunction<>("FULL", group, k);
            result = space.executeFunction(centroidFunc, strategy);
            isCalculated = true;
        }

        if (result != null && !result.isEmpty()) {
            visualizer.highlightItems(group, "#2A9D8F");

            List<T> closestIds = result.stream().map(ItemDistance::getId).collect(Collectors.toList());
            visualizer.highlightItems(closestIds, "#8B5CF6");

            StringBuilder sb = new StringBuilder("Centroid calculated. Top " + k + " closest:\n");
            for (ItemDistance<T> res : result) {
                sb.append("- ").append(res.getId()).append(String.format(" (Distance: %.4f)\n", res.getDistance()));
            }

            return sb.toString() + "\n(Strategy: " + strategy.toString() + ")";
        }
        return "No centroid found.";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CentroidAction<?> that = (CentroidAction<?>) obj;

        return k == that.k && group.containsAll(that.group) && group.size() == that.group.size() && this.strategy.getClass().equals(that.strategy.getClass());
    }
}