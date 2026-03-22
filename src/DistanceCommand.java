import java.util.List;

public class DistanceCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T w1, w2;

    public DistanceCommand(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T w1, T w2) {
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

        // צובעים רק את שתי המילים! אין יותר קווים.
        visualizer.highlightItems(List.of(w1), "#00FFFF"); // תכלת
        visualizer.highlightItems(List.of(w2), "#FF00FF"); // סגול

        return "המרחק בין '" + w1 + "' ל-'" + w2 + "': " + String.format("%.5f", dist);
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }
}