package actions;

import core.AbstractAnalyzableSpace;
import math.DistanceStrategy;
import visuals.SpaceVisualizer;

public abstract class AbstractAnalysisAction<T> implements AppAction<T> {

    protected AbstractAnalyzableSpace<T> space;
    protected SpaceVisualizer<T> visualizer;
    protected DistanceStrategy strategy;
    protected boolean isCalculated = false;

    public AbstractAnalysisAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy) {
        this.space = space;
        this.visualizer = visualizer;
        this.strategy = strategy;
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }
}