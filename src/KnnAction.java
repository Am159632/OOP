import java.util.List;
import java.util.stream.Collectors;

public class KnnAction<T> implements AppAction<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T target;
    private int k;

    public KnnAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T target, int k) {
        this.space = space;
        this.visualizer = visualizer;
        this.strategy = strategy;
        this.target = target;
        this.k = k;
    }

    @Override
    public String execute() {
        KnnFunction<T> knnFunc = new KnnFunction<>("FULL", target, k);
        List<ItemDistance<T>> neighbors = space.executeFunction(knnFunc, strategy);
        List<T> neighborIds = neighbors.stream().map(ItemDistance::getId).collect(Collectors.toList());

        visualizer.highlightItems(List.of(target), "#FF0000");
        visualizer.highlightItems(neighborIds, "#32CD32");

        StringBuilder sb = new StringBuilder("Neighbors of '" + target + "':\n");
        for (int i = 0; i < neighbors.size(); i++) {
            sb.append(i + 1).append(". ").append(neighbors.get(i).getId())
                    .append(" (Distance: ").append(String.format("%.4f", neighbors.get(i).getDistance())).append(")\n");
        }
        return sb.toString()+" (Distance: " + strategy.toString() + ")";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }

}