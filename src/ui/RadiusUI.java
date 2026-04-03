package ui;

import core.*;
import math.*;
import actions.*;
import visuals.*;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.util.List;

public class RadiusUI<T> extends AbstractSpaceCommand<T> {
    private ComboBox<T> comboTarget;
    private TextField txtRadius;

    public RadiusUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        super(space);
        comboTarget = UIUtils.createSearchableComboBox(vocabulary);
        txtRadius = new TextField();
        txtRadius.setPromptText("Max Distance");

        uiContainer.getChildren().addAll(
                UIUtils.createClearableComboRow(comboTarget, "Target Item"),
                txtRadius
        );
    }

    @Override
    public String getName() { return "Radius Search"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        T target = getComboValue(comboTarget);
        if (target == null) throw new IllegalArgumentException("Empty Target");

        double radius = Double.parseDouble(txtRadius.getText());
        return new RadiusAction<>(space, visualizer, strategy, target, radius);
    }

    @Override
    public void onNodeClicked(T item) {
        fillFirstEmptyCombo(item, List.of(comboTarget));
    }
}