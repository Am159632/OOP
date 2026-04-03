package ui;

import core.*;
import actions.*;

import javafx.scene.control.ComboBox;
import visuals.SpaceVisualizer;

import java.util.List;

public class AnalogyUI<T> extends AbstractSpaceCommand<T> {
    private ComboBox<T> comboW1, comboW2, comboW3;

    public AnalogyUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        super(space);
        comboW1 = UIUtils.createSearchableComboBox(vocabulary);
        comboW2 = UIUtils.createSearchableComboBox(vocabulary);
        comboW3 = UIUtils.createSearchableComboBox(vocabulary);

        uiContainer.getChildren().addAll(
                UIUtils.createClearableComboRow(comboW1, "Item 1"),
                UIUtils.createClearableComboRow(comboW2, "Item 2"),
                UIUtils.createClearableComboRow(comboW3, "Item 3")
        );
    }

    @Override
    public String getName() { return "Analogy"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        T w1 = getComboValue(comboW1);
        T w2 = getComboValue(comboW2);
        T w3 = getComboValue(comboW3);

        if (w1 == null || w2 == null || w3 == null) throw new IllegalArgumentException("Empty Inputs");

        return new AnalogyAction<>(space, visualizer, strategy, w1, w2, w3);
    }

    @Override
    public void onNodeClicked(T item) {
        fillFirstEmptyCombo(item, List.of(comboW1, comboW2, comboW3));
    }
}