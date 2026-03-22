import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;

public interface SpaceVisualizer<T> {
    void drawSpace(List<PointData<T>> points);
    void highlightItems(List<T> items, String colorHex);
    void clearHighlights();
    void clearSpace();
    Node getVisualNode();
    void setOnNodeClicked(Consumer<T> listener);
}