package ui;

import core.*;
import math.*;
import actions.*;
import visuals.*;

import javafx.scene.control.ComboBox;
import java.util.List;

public class DistanceUI<T> extends AbstractSpaceCommand<T> {
    private ComboBox<T> comboW1, comboW2;

    public DistanceUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        super(space);
        comboW1 = UIUtils.createSearchableComboBox(vocabulary);
        comboW2 = UIUtils.createSearchableComboBox(vocabulary);

        uiContainer.getChildren().addAll(
                UIUtils.createClearableComboRow(comboW1, "Item 1"),
                UIUtils.createClearableComboRow(comboW2, "Item 2")
        );
    }

    @Override
    public String getName() { return "Calculate Distance"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        T w1 = getComboValue(comboW1);
        T w2 = getComboValue(comboW2);

        if (w1 == null || w2 == null) throw new IllegalArgumentException("Empty Inputs");

        return new DistanceAction<>(space, visualizer, strategy, w1, w2);
    }

    @Override
    public void onNodeClicked(T item) {
        fillFirstEmptyCombo(item, List.of(comboW1, comboW2));
    }
}