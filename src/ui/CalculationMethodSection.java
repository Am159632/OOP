package ui;

import math.DistanceStrategy;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;

public class CalculationMethodSection<T> extends AbstractMenuSection {
    private AppUIManager<T> uiManager;

    public CalculationMethodSection(AppUIManager<T> uiManager) {
        super("Calculation Method");
        this.uiManager = uiManager;
    }

    @Override
    protected void buildContent(VBox container) {
        List<DistanceStrategy> strategies = new ArrayList<>(uiManager.getStrategies());
        ComboBox<DistanceStrategy> distanceBox = UIUtils.createSearchableComboBox(strategies);

        if (!strategies.isEmpty()) {
            distanceBox.setValue(strategies.get(0));
            uiManager.setStrategy(distanceBox.getValue());
        }

        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> {
            if (distanceBox.getValue() != null) {
                uiManager.setStrategy(distanceBox.getValue());
            }
        });

        container.getChildren().add(distanceBox);
    }
}