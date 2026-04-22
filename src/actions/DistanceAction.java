package actions;

import core.*;
import math.*;
import visuals.HighlightColors;
import visuals.SpaceVisualizer;

import java.util.List;

public class DistanceAction<T> extends AbstractAnalysisAction<T> {
    private T w1;
    private T w2;
    private double dist;

    public DistanceAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T w1, T w2) {
        super(space, visualizer, strategy);
        this.w1 = w1;
        this.w2 = w2;
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            DistanceFunction<T> func = new DistanceFunction<>(SpaceNames.FULL, w1, w2);
            dist = space.executeFunction(func, strategy);
            isCalculated = true;
        }

        visualizer.highlightItems(List.of(w1, w2), HighlightColors.PRIMARY);
        visualizer.drawLine(w1, w2, HighlightColors.WARNING, 3.0);

        return "Distance between '" + w1 + "' and '" + w2 + "': " + String.format("%.5f", dist) + " (Strategy: " + strategy.getName() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DistanceAction<?> that = (DistanceAction<?>) obj;

        return this.w1.equals(that.w1) &&
                this.w2.equals(that.w2) &&
                this.strategy.getClass().equals(that.strategy.getClass());
    }
}