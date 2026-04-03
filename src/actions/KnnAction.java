package actions;

import core.*;
import math.*;
import visuals.SpaceVisualizer;

import java.util.List;
import java.util.stream.Collectors;

public class KnnAction<T> extends AbstractAnalysisAction<T> {
    private T target;
    private int k;
    private List<ItemDistance<T>> neighbors;
    private String output;

    public KnnAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T target, int k) {
        super(space, visualizer, strategy);
        this.target = target;
        this.k = k;
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            KnnFunction<T> knnFunc = new KnnFunction<>("FULL", target, k);
            neighbors = space.executeFunction(knnFunc, strategy);

            StringBuilder sb = new StringBuilder("Neighbors of '" + target + "':\n");
            for (int i = 0; i < neighbors.size(); i++) {
                sb.append(i + 1).append(". ").append(neighbors.get(i).getId())
                        .append(" (Distance: ").append(String.format("%.4f", neighbors.get(i).getDistance())).append(")\n");
            }
            output = sb.toString() + "\n(Strategy: " + strategy.toString() + ")";
            isCalculated = true;
        }

        List<T> neighborIds = neighbors.stream().map(ItemDistance::getId).collect(Collectors.toList());
        visualizer.highlightItems(List.of(target), "#007BFF");
        visualizer.highlightItems(neighborIds, "#28A745");

        double maxDist = neighbors.isEmpty() ? 1.0 : neighbors.get(neighbors.size() - 1).getDistance();
        double minDist = neighbors.isEmpty() ? 0.0 : neighbors.get(0).getDistance();

        for (ItemDistance<T> neighbor : neighbors) {
            double thickness = 3.0;
            if (maxDist != minDist) {
                double normalized = 1.0 - ((neighbor.getDistance() - minDist) / (maxDist - minDist));
                thickness = 1.0 + (normalized * 4.0);
            }
            visualizer.drawLine(target, neighbor.getId(), "#FD7E14", thickness);
        }

        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        KnnAction<?> that = (KnnAction<?>) obj;

        return this.k == that.k &&
                this.target.equals(that.target) &&
                this.strategy.getClass().equals(that.strategy.getClass());
    }
}