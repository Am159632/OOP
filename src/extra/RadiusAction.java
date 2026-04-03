package extra;

import actions.AbstractAnalysisAction;
import core.*;
import math.*;
import visuals.SpaceVisualizer;

import java.util.List;

public class RadiusAction<T> extends AbstractAnalysisAction<T> {
    private T target;
    private double radius;
    private List<T> foundItems;

    public RadiusAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T target, double radius) {
        super(space, visualizer, strategy);
        this.target = target;
        this.radius = radius;
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            RadiusFunction<T> func = new RadiusFunction<>("FULL", target, radius);
            foundItems = space.executeFunction(func, strategy);
            isCalculated = true;
        }

        visualizer.highlightItems(List.of(target), "#007BFF");
        visualizer.highlightItems(foundItems, "#28A745");

        for (T item : foundItems) {
            visualizer.drawLine(target, item, "#A9A9A9", 1.5);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Radius Search for '").append(target).append("' (Max Distance: ").append(radius).append("):\n");
        sb.append(foundItems.isEmpty() ? "No items found in this radius." : foundItems.toString());
        sb.append("\n[Strategy: ").append(strategy.getClass().getSimpleName()).append("]");

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RadiusAction<?> that = (RadiusAction<?>) obj;
        return Double.compare(this.radius, that.radius) == 0 &&
                this.target.equals(that.target) &&
                this.strategy.getClass().equals(that.strategy.getClass());
    }
}