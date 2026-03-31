package visuals;

import core.*;
import math.*;
import actions.*;
import ui.*;

import java.util.List;
import java.util.ArrayList;
import javafx.scene.Node;
import java.util.function.Consumer;

public class MultiSpaceVisualizer<T> implements SpaceVisualizer<T> {

    private List<SpaceVisualizer<T>> visualizers;

    public MultiSpaceVisualizer(List<SpaceVisualizer<T>> visualizers) {
        this.visualizers = new ArrayList<>(visualizers);
    }

    @Override
    public void drawSpace(List<PointData<T>> points) {
        for (SpaceVisualizer<T> vis : visualizers) vis.drawSpace(points);
    }

    @Override
    public void highlightItems(List<T> items, String colorHex) {
        for (SpaceVisualizer<T> vis : visualizers) vis.highlightItems(items, colorHex);
    }

    @Override
    public void clearHighlights() {
        for (SpaceVisualizer<T> vis : visualizers) vis.clearHighlights();
    }

    @Override
    public void clearSpace() {
        for (SpaceVisualizer<T> vis : visualizers) vis.clearSpace();
    }

    @Override
    public void clearScene() {
        for (SpaceVisualizer<T> vis : visualizers) vis.clearScene();
    }

    @Override
    public void drawNode(T id, double normX, double normY, double normZ) {
        for (SpaceVisualizer<T> vis : visualizers) vis.drawNode(id, normX, normY, normZ);
    }

    @Override
    public void setOnNodeClicked(Consumer<T> listener) {
        for (SpaceVisualizer<T> vis : visualizers) vis.setOnNodeClicked(listener);
    }

    @Override
    public Node getVisualNode() {
        return null;
    }

    @Override
    public void setZoom(double percentage) {
    }

    @Override
    public void drawLine(T source, T target, String colorHex, double thickness) {
        for (SpaceVisualizer<T> vis : visualizers) vis.drawLine(source,target,colorHex,thickness);
    }

}