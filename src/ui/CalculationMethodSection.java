package ui;

import math.DistanceStrategy;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CalculationMethodSection<T> extends AbstractMenuSection {
    private AppUIManager<T> uiManager;

    public CalculationMethodSection(AppUIManager<T> uiManager) {
        super("Calculation Method");
        this.uiManager = uiManager;
    }

    @Override
    protected void buildContent(VBox container) {
        ComboBox<DistanceStrategy> distanceBox = new ComboBox<>();
        distanceBox.getItems().addAll(uiManager.getStrategies());

        if (!distanceBox.getItems().isEmpty()) {
            distanceBox.setValue(distanceBox.getItems().get(0));
            uiManager.setStrategy(distanceBox.getValue());
        }

        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> uiManager.setStrategy(distanceBox.getValue()));

        container.getChildren().add(distanceBox);
    }
}