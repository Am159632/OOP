import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticLineAction<T> implements AppAction<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T start;
    private T end;
    private int k;

    public SemanticLineAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T start, T end, int k) {
        this.space = space;
        this.visualizer = visualizer;
        this.strategy = strategy;
        this.start = start;
        this.end = end;
        this.k = k;
    }

    @Override
    public String execute() {
        ProjectionFunction<T> func = new ProjectionFunction<>("FULL", start, end);
        List<ItemDistance<T>> projections = space.executeFunction(func, strategy);

        projections.sort(Comparator.comparingDouble(ItemDistance::getDistance));

        List<T> closeToStart = projections.stream().limit(k).map(ItemDistance::getId).collect(Collectors.toList());
        List<T> closeToEnd = projections.stream().skip(Math.max(0, projections.size() - k)).map(ItemDistance::getId).collect(Collectors.toList());
        Collections.reverse(closeToEnd);

        visualizer.clearHighlights();
        visualizer.highlightItems(closeToStart, "#FF4500");
        visualizer.highlightItems(closeToEnd, "#32CD32");

        StringBuilder sb = new StringBuilder();
        sb.append("Semantic Line: ").append(start).append(" <--> ").append(end).append("\n\n");
        sb.append("Top ").append(k).append(" closest to '").append(start).append("':\n");
        closeToStart.forEach(word -> sb.append("- ").append(word).append("\n"));
        sb.append("\nTop ").append(k).append(" closest to '").append(end).append("':\n");
        closeToEnd.forEach(word -> sb.append("- ").append(word).append("\n"));

        return sb.toString();
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }

    @Override
    public String getName() {
        return "Semantic Line";
    }
}