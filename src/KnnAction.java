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

        visualizer.highlightItems(List.of(target), "#007BFF");
        visualizer.highlightItems(neighborIds, "#28A745");

        double maxDist = neighbors.isEmpty() ? 1.0 : neighbors.get(neighbors.size() - 1).getDistance();
        double minDist = neighbors.isEmpty() ? 0.0 : neighbors.get(0).getDistance();

        for (ItemDistance<T> neighbor : neighbors) {
            double thickness = 3.0;
            if (maxDist != minDist) {
                double normalized = 1.0 - ((neighbor.getDistance() - minDist) / (maxDist - minDist));
                thickness = 1.0 + (normalized * 4.0);
            }
            visualizer.drawLine(target, neighbor.getId(), "#FD7E14", thickness);
        }

        StringBuilder sb = new StringBuilder("Neighbors of '" + target + "':\n");
        for (int i = 0; i < neighbors.size(); i++) {
            sb.append(i + 1).append(". ").append(neighbors.get(i).getId())
                    .append(" (Distance: ").append(String.format("%.4f", neighbors.get(i).getDistance())).append(")\n");
        }
        return sb.toString() + "\n(Strategy: " + strategy.toString() + ")";
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        KnnAction<?> that = (KnnAction<?>) obj;

        return this.k == that.k &&
                this.target.equals(that.target) &&
                this.strategy.getClass().equals(that.strategy.getClass());
    }
}