package ui;

import core.*;
import actions.*;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import visuals.SpaceVisualizer;

import java.util.List;

public class KnnUI<T> extends AbstractSpaceCommand<T> {
    private ComboBox<T> comboTarget;
    private TextField txtK;

    public KnnUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        super(space);
        comboTarget = UIUtils.createSearchableComboBox(vocabulary);
        txtK = new TextField();
        txtK.setPromptText("Number of Neighbors (K)");

        uiContainer.getChildren().addAll(
                UIUtils.createClearableComboRow(comboTarget, "Target Item"),
                txtK
        );
    }

    @Override
    public String getName() { return "Find Neighbors (KNN)"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        T target = getComboValue(comboTarget);
        if (target == null) throw new IllegalArgumentException("Empty Target");

        int k = Integer.parseInt(txtK.getText());
        return new KnnAction<>(space, visualizer, strategy, target, k);
    }

    @Override
    public void onNodeClicked(T item) {
        fillFirstEmptyCombo(item, List.of(comboTarget));
    }
}