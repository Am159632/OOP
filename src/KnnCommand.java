import java.util.List;
import java.util.stream.Collectors;

public class KnnCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T targetId;
    private int k;

    public KnnCommand(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T targetId, int k) {
        this.space = space;
        this.visualizer = visualizer;
        this.strategy = strategy;
        this.targetId = targetId;
        this.k = k;
    }

    @Override
    public String execute() {
        KnnFunction<T> knnFunc = new KnnFunction<>("FULL", targetId, k);
        List<ItemDistance<T>> neighbors = space.executeFunction(knnFunc, strategy);

        List<T> neighborIds = neighbors.stream().map(ItemDistance::getId).collect(Collectors.toList());

        // צובעים את המטרה ואת השכנים
        visualizer.highlightItems(List.of(targetId), "#FF0000"); // אדום
        visualizer.highlightItems(neighborIds, "#32CD32");       // ירוק

        // בונים את הטקסט למסך
        StringBuilder sb = new StringBuilder("השכנים של '" + targetId + "':\n");
        for (int i = 0; i < neighbors.size(); i++) {
            sb.append(i + 1).append(". ").append(neighbors.get(i).getId())
                    .append(" (מרחק: ").append(String.format("%.4f", neighbors.get(i).getDistance())).append(")\n");
        }
        return sb.toString();
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }
}