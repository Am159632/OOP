package ui;

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
        ComboBox<String> distanceBox = new ComboBox<>();
        distanceBox.getItems().addAll(uiManager.getStrategies().keySet());
        distanceBox.setValue("Euclidean");
        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> uiManager.setStrategy(uiManager.getStrategies().get(distanceBox.getValue())));

        container.getChildren().add(distanceBox);
    }
}