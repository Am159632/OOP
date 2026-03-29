import java.util.List;

public class CentroidAction<T> implements AppAction<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private List<T> group;

    public CentroidAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, List<T> group) {
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
            visualizer.highlightItems(group, "#ADD8E6");
            visualizer.highlightItems(List.of(result), "#FF0000");
            return "Centroid of the group is: " + result;
        }
        return "No centroid found.";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }

    @Override
    public String getName() {
        return "Centroid (Average)";
    }
}