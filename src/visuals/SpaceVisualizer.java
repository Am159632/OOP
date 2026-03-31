package visuals;

import core.*;
import math.*;
import actions.*;
import ui.*;

import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;

public interface SpaceVisualizer<T> {
    void drawSpace(List<PointData<T>> points);
    void highlightItems(List<T> items, String colorHex);
    void clearHighlights();
    void clearSpace();
    Node getVisualNode();
    void clearScene();
    void drawNode(T id, double normX, double normY, double normZ);
    void setOnNodeClicked(Consumer<T> listener);
    void setZoom(double percentage);
    void drawLine(T source, T target, String colorHex, double thickness);
}