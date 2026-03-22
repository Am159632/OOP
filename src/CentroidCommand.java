import java.util.List;

public class CentroidCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private List<T> group;

    public CentroidCommand(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, List<T> group) {
        this.space = space;
        this.visualizer = visualizer;
        this.strategy = strategy;
        this.group = group;
    }

    @Override
    public String execute() {
        CentroidFunction<T> centroidFunc = new CentroidFunction<>("FULL", group);
        T result = space.executeFunction(centroidFunc, strategy);

        if (result != null) {
            // צובעים את הקבוצה שהוזנה
            visualizer.highlightItems(group, "#ADD8E6"); // תכלת בהיר
            // צובעים את התוצאה (המרכז)
            visualizer.highlightItems(List.of(result), "#FF0000"); // אדום בולט

            return "מילת המרכז של הקבוצה היא: " + result;
        }
        return "לא נמצא מרכז לקבוצה.";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }
}