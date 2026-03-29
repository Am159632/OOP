import java.util.List;

public class DistanceAction<T> implements AppAction<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T w1;
    private T w2;

    public DistanceAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T w1, T w2) {
        this.space = space;
        this.visualizer = visualizer;
        this.strategy = strategy;
        this.w1 = w1;
        this.w2 = w2;
    }

    @Override
    public String execute() {
        DistanceFunction<T> func = new DistanceFunction<>("FULL", w1, w2);
        double dist = space.executeFunction(func, strategy);

        visualizer.highlightItems(List.of(w1, w2), "#007BFF");
        visualizer.drawLine(w1, w2, "#FD7E14", 3.0);

        return "Distance between '" + w1 + "' and '" + w2 + "': " + String.format("%.5f", dist) + " (Strategy: " + strategy.toString() + ")";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
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