package ui;

import visuals.GUIVisualizer;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ZoomSection<T> extends AbstractMenuSection {
    private ComboBox<GUIVisualizer<T>> viewSelector;

    public ZoomSection(ComboBox<GUIVisualizer<T>> viewSelector) {
        super("Camera Zoom Level");
        this.viewSelector = viewSelector;
    }

    @Override
    protected void buildContent(VBox container) {
        Slider zoomSlider = new Slider(1, 100, 50);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setMajorTickUnit(25);

        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            GUIVisualizer<T> active = viewSelector.getValue();
            if (active != null && Math.abs(active.getCurrentZoom() - newVal.doubleValue()) > 0.1) {
                active.setZoom(newVal.doubleValue());
            }
        });

        viewSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                zoomSlider.setValue(newVal.getCurrentZoom());
                newVal.setOnZoomChanged(val -> zoomSlider.setValue(val));
            }
        });

        if (viewSelector.getValue() != null) {
            viewSelector.getValue().setOnZoomChanged(val -> zoomSlider.setValue(val));
        }

        container.getChildren().add(zoomSlider);
    }
}