import java.util.List;

public class AnalogyAction<T> implements AppAction<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T w1, w2, w3;

    public AnalogyAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T w1, T w2, T w3) {
        this.space = space;
        this.visualizer = visualizer;
        this.strategy = strategy;
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
    }

    @Override
    public String execute() {
        AnalogyFunction<T> analogyFunc = new AnalogyFunction<>("FULL", w1, w2, w3);
        T result = space.executeFunction(analogyFunc, strategy);

        if (result != null) {
            visualizer.highlightItems(List.of(w1, w2, w3), "#FFA500");
            visualizer.highlightItems(List.of(result), "#FFD700");
            return w1 + " - " + w2 + " + " + w3 + " = " + result;
        }
        return "No analogy found.";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AnalogyAction<?> that = (AnalogyAction<?>) obj;

        boolean w2Equals = this.w2.equals(that.w2);
        boolean w1w3Match = (this.w1.equals(that.w1) && this.w3.equals(that.w3)) ||
                (this.w1.equals(that.w3) && this.w3.equals(that.w1));

        return w2Equals && w1w3Match && this.strategy.getClass().equals(that.strategy.getClass());
    }

}