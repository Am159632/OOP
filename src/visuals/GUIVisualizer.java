package visuals;

import javafx.scene.Node;
import java.util.function.Consumer;

public interface GUIVisualizer<T> {
    Node getVisualNode();
    void setZoom(double percentage);
    double getCurrentZoom();
    void setOnZoomChanged(Consumer<Double> listener);
    int getDimensions();
}