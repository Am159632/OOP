package ui;

import visuals.GUIVisualizer;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ZoomSection<T> implements MenuSection {
    private ComboBox<GUIVisualizer<T>> viewSelector;

    public ZoomSection(ComboBox<GUIVisualizer<T>> viewSelector) {
        this.viewSelector = viewSelector;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblZoom = new Label("Camera Zoom Level");
        lblZoom.getStyleClass().add("section-title");

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

        box.getChildren().addAll(lblZoom, zoomSlider);
        return box;
    }
}