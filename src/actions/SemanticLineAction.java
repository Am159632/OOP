package actions;

import core.*;
import math.*;
import visuals.SpaceVisualizer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticLineAction<T> extends AbstractAnalysisAction<T> {
    private T start, end;
    private List<T> orderedItems;
    private String output;

    public SemanticLineAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T start, T end) {
        super(space, visualizer, strategy);
        this.start = start;
        this.end = end;
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            ProjectionFunction<T> func = new ProjectionFunction<>("FULL", start, end);
            List<ItemDistance<T>> projections = space.executeFunction(func, strategy);

            projections.sort(Comparator.comparingDouble(ItemDistance::getDistance));
            orderedItems = projections.stream().map(ItemDistance::getId).collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            sb.append("Semantic Line Order: ").append(start).append(" -> ").append(end).append("\n\n");
            sb.append("1. ").append(start).append("\n");
            for (int i = 0; i < orderedItems.size(); i++) {
                sb.append(i + 2).append(". ").append(orderedItems.get(i)).append("\n");
            }
            sb.append(orderedItems.size() + 2).append(". ").append(end).append("\n");
            output = sb.toString() + "\n(Strategy: " + strategy.toString() + ")";

            isCalculated = true;
        }

        visualizer.clearHighlights();
        visualizer.highlightItems(List.of(start), "#2A9D8F");
        visualizer.highlightItems(List.of(end), "#E76F51");

        visualizer.drawLine(start, end, "#4F46E5", 4.0);

        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SemanticLineAction<?> that = (SemanticLineAction<?>) obj;
        return this.start.equals(that.start)
                && this.end.equals(that.end)
                && this.strategy.getClass().equals(that.strategy.getClass());
    }
}