import javafx.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractSpaceVisualizer<T, V extends Node> implements SpaceVisualizer<T> {

    protected Map<T, V> nodesMap = new HashMap<>();
    protected Map<T, String> highlightedColors = new HashMap<>();
    protected List<Node> drawnLines = new ArrayList<>();
    private String viewName;
    private Consumer<T> onNodeClickedListener;

    public AbstractSpaceVisualizer(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public void setOnNodeClicked(Consumer<T> listener) {
        this.onNodeClickedListener = listener;
    }

    public void drawSpace(List<PointData<T>> points) {
        clearSpace();
        if (points == null || points.isEmpty()) return;

        double[] mins = calculateMins(points);
        double[] maxs = calculateMaxs(points);

        for (PointData<T> p : points) {
            double[] coords = p.getCoordinates();
            double normX = (maxs[0] == mins[0]) ? 0.5 : (coords[0] - mins[0]) / (maxs[0] - mins[0]);
            double normY = coords.length > 1 ? ((maxs[1] == mins[1]) ? 0.5 : (coords[1] - mins[1]) / (maxs[1] - mins[1])) : 0.5;
            double normZ = coords.length > 2 ? ((maxs[2] == mins[2]) ? 0.5 : (coords[2] - mins[2]) / (maxs[2] - mins[2])) : 0.5;

            drawNode(p.getId(), normX, normY, normZ);
        }
    }

    @Override
    public final void drawNode(T id, double normX, double normY, double normZ) {
        V shape = createShape(id, normX, normY, normZ);

        shape.setOnMouseClicked(e -> {
            if (onNodeClickedListener != null) {
                onNodeClickedListener.accept(id);
            }
        });

        addShapeToScene(shape);
        nodesMap.put(id, shape);
    }

    @Override
    public void highlightItems(List<T> items, String colorHex) {
        for (T item : items) {
            V shape = nodesMap.get(item);
            if (shape != null) {
                applyHighlight(shape, colorHex);
                highlightedColors.put(item, colorHex);
            }
        }
    }

    @Override
    public void clearHighlights() {
        for (T item : highlightedColors.keySet()) {
            V shape = nodesMap.get(item);
            if (shape != null){
                removeHighlight(shape);
            }
        }
        highlightedColors.clear();

        for (Node line : drawnLines) {
            removeDrawnLine(line);
        }
        drawnLines.clear();
    }

    public void clearSpace() {
        nodesMap.clear();
        highlightedColors.clear();
        drawnLines.clear();
        clearScene();
    }

    private double[] calculateMins(List<PointData<T>> points) {
        int dims = points.get(0).getCoordinates().length;
        double[] mins = new double[dims];
        for (int i = 0; i < dims; i++) mins[i] = Double.MAX_VALUE;
        for (PointData<T> p : points) {
            for (int i = 0; i < Math.min(dims, p.getCoordinates().length); i++) {
                mins[i] = Math.min(mins[i], p.getCoordinates()[i]);
            }
        }
        return mins;
    }

    private double[] calculateMaxs(List<PointData<T>> points) {
        int dims = points.get(0).getCoordinates().length;
        double[] maxs = new double[dims];
        for (int i = 0; i < dims; i++) maxs[i] = -Double.MAX_VALUE;
        for (PointData<T> p : points) {
            for (int i = 0; i < Math.min(dims, p.getCoordinates().length); i++) {
                maxs[i] = Math.max(maxs[i], p.getCoordinates()[i]);
            }
        }
        return maxs;
    }

    @Override
    public String toString() {
        return viewName;
    }

    protected abstract V createShape(T id, double normX, double normY, double normZ);
    protected abstract void addShapeToScene(V shape);
    protected abstract void applyHighlight(V shape, String colorHex);
    protected abstract void removeHighlight(V shape);
    protected abstract String getDefaultColor();
    protected abstract void removeDrawnLine(Node line);

    public abstract void clearScene();
    public abstract Node getVisualNode();
}