import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ZoomSection<T> implements MenuSection {
    private ComboBox<SpaceVisualizer<T>> viewSelector;

    public ZoomSection(ComboBox<SpaceVisualizer<T>> viewSelector) {
        this.viewSelector = viewSelector;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblZoom = new Label("4. Camera Zoom Level");
        lblZoom.getStyleClass().add("section-title");

        Slider zoomSlider = new Slider(1, 100, 50);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setMajorTickUnit(25);

        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SpaceVisualizer<T> active = viewSelector.getValue();
            if (active != null) active.setZoom(newVal.doubleValue());
        });

        box.getChildren().addAll(lblZoom, zoomSlider);
        return box;
    }
}