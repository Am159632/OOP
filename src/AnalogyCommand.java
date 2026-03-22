import java.util.List;

public class AnalogyCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T w1, w2, w3;

    public AnalogyCommand(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T w1, T w2, T w3) {
        this.space = space; this.visualizer = visualizer; this.strategy = strategy;
        this.w1 = w1; this.w2 = w2; this.w3 = w3;
    }

    @Override
    public String execute() {
        AnalogyFunction<T> analogyFunc = new AnalogyFunction<>("FULL", w1, w2, w3);
        T result = space.executeFunction(analogyFunc, strategy);

        if (result != null) {
            // צובע את 3 מילות השאלה בכתום
            visualizer.highlightItems(List.of(w1, w2, w3), "#FFA500");
            // צובע את התשובה בזהב
            visualizer.highlightItems(List.of(result), "#FFD700");

            return w1 + " - " + w2 + " + " + w3 + " = " + result;
        }
        return "לא נמצאה תשובה לאנלוגיה.";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights(); // מוחק את הצבעים בלחיצת כפתור אחת!
    }
}