package extra;

import actions.AbstractAnalysisAction;
import core.*;
import math.*;
import visuals.SpaceVisualizer;

import java.util.List;

public class RadiusAction<T> extends AbstractAnalysisAction<T> {
    private T target;
    private double minradius;
    private double maxradius;
    private List<T> foundItems;
    private boolean swapped;

    public RadiusAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T target, double minradius, double maxradius) {
        super(space, visualizer, strategy);
        this.target = target;

        if (minradius > maxradius) {
            this.minradius = maxradius;
            this.maxradius = minradius;
            this.swapped = true;
        } else {
            this.minradius = minradius;
            this.maxradius = maxradius;
            this.swapped = false;
        }
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            RadiusFunction<T> func = new RadiusFunction<>("FULL", target, minradius, maxradius);
            foundItems = space.executeFunction(func, strategy);
            isCalculated = true;
        }

        visualizer.highlightItems(List.of(target), "#2A9D8F");
        visualizer.highlightItems(foundItems, "#8B5CF6");

        for (T item : foundItems) {
            visualizer.drawLine(target, item, "#4F46E5", 1.5);
        }

        StringBuilder sb = new StringBuilder();

        if (swapped) {
            sb.append("[Notice: Min was greater than Max. Values were automatically swapped.]\n");
        }

        sb.append("Radius Search for '").append(target).append("' (Range: ").append(minradius).append(" - ").append(maxradius).append("):\n");

        if (foundItems.isEmpty()) {
            sb.append("No items found in this radius.");
        } else {
            sb.append("Total items found: ").append(foundItems.size()).append("\n");
            sb.append(foundItems.toString());
        }

        sb.append("\n[Strategy: ").append(strategy.getClass().getSimpleName()).append("]");

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RadiusAction<?> that = (RadiusAction<?>) obj;
        return Double.compare(this.minradius, that.minradius) == 0 &&
                Double.compare(this.maxradius, that.maxradius) == 0 &&
                this.target.equals(that.target) &&
                this.strategy.getClass().equals(that.strategy.getClass());
    }
}