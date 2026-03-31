package ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CalculationMethodSection<T> implements MenuSection {
    private AppUIManager<T> uiManager;

    public CalculationMethodSection(AppUIManager<T> uiManager) {
        this.uiManager = uiManager;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblDist = new Label("Calculation Method");
        lblDist.getStyleClass().add("section-title");

        ComboBox<String> distanceBox = new ComboBox<>();
        distanceBox.getItems().addAll(uiManager.getStrategies().keySet());
        distanceBox.setValue("Euclidean");
        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> uiManager.setStrategy(uiManager.getStrategies().get(distanceBox.getValue())));

        box.getChildren().addAll(lblDist, distanceBox);
        return box;
    }
}