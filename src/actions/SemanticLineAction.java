package actions;

import core.*;
import math.*;
import visuals.SpaceVisualizer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticLineAction<T> extends AbstractAnalysisAction<T> {
    private T start, end;
    private int k;
    private List<T> closeToStart;
    private List<T> closeToEnd;
    private String output;

    public SemanticLineAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T start, T end, int k) {
        super(space, visualizer, strategy);
        this.start = start;
        this.end = end;
        this.k = k;
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            ProjectionFunction<T> func = new ProjectionFunction<>("FULL", start, end);
            List<ItemDistance<T>> projections = space.executeFunction(func, strategy);

            projections.sort(Comparator.comparingDouble(ItemDistance::getDistance));

            closeToStart = projections.stream().limit(k).map(ItemDistance::getId).collect(Collectors.toList());
            closeToEnd = projections.stream().skip(Math.max(0, projections.size() - k)).map(ItemDistance::getId).collect(Collectors.toList());
            Collections.reverse(closeToEnd);

            StringBuilder sb = new StringBuilder();
            sb.append("Semantic Line: ").append(start).append(" <--> ").append(end).append("\n\n");
            sb.append("Top ").append(k).append(" closest to '").append(start).append("':\n");
            closeToStart.forEach(word -> sb.append("- ").append(word).append("\n"));
            sb.append("\nTop ").append(k).append(" closest to '").append(end).append("':\n");
            closeToEnd.forEach(word -> sb.append("- ").append(word).append("\n"));
            output = sb.toString() + "\n(Strategy: " + strategy.toString() + ")";

            isCalculated = true;
        }

        visualizer.highlightItems(List.of(start, end), "#007BFF");
        visualizer.highlightItems(closeToStart, "#28A745");
        visualizer.highlightItems(closeToEnd, "#DC3545");

        visualizer.drawLine(start, end, "#007BFF", 4.0);

        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SemanticLineAction<?> that = (SemanticLineAction<?>) obj;
        boolean match = (this.start.equals(that.start) && this.end.equals(that.end));
        boolean op = (this.start.equals(that.end) && this.end.equals(that.start));
        return (match || op) && k == that.k && this.strategy.getClass().equals(that.strategy.getClass());
    }
}