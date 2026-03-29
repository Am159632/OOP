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
            visualizer.highlightItems(group, "#007BFF");
            visualizer.highlightItems(List.of(result), "#28A745");

            for (T member : group) {
                visualizer.drawLine(member, result, "#A9A9A9", 1.0);
            }

            return "Centroid of the group is: " + result + " (Strategy: " + strategy.toString() + ")";
        }
        return "No centroid found.";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CentroidAction<?> that = (CentroidAction<?>) obj;

        return group.containsAll(that.group) && group.size()==that.group.size() && this.strategy.getClass().equals(that.strategy.getClass());
    }
}