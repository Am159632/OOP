package ui;

import core.*;
import actions.*;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import visuals.SpaceVisualizer;

import java.util.List;
import java.util.function.Function;

public class SemanticLineUI<T> extends AbstractSpaceCommand<T> {
    private ComboBox<T> comboStart, comboEnd;
    private TextField txtK;

    public SemanticLineUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary, Function<String, T> parser) {
        super(space, parser);
        comboStart = UIUtils.createSearchableComboBox(vocabulary);
        comboEnd = UIUtils.createSearchableComboBox(vocabulary);
        txtK = new TextField();
        txtK.setPromptText("Amount of Steps (K)");

        uiContainer.getChildren().addAll(
                UIUtils.createClearableComboRow(comboStart, "Start Item"),
                UIUtils.createClearableComboRow(comboEnd, "End Item"),
                txtK
        );
    }

    @Override
    public String getName() { return "Semantic Line"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        T start = getComboValue(comboStart);
        T end = getComboValue(comboEnd);

        if (start == null || end == null) throw new IllegalArgumentException("Empty Inputs");
        int k = Integer.parseInt(txtK.getText());

        return new SemanticLineAction<>(space, visualizer, strategy, start, end, k);
    }

    @Override
    public void onNodeClicked(T item) {
        fillFirstEmptyCombo(item, List.of(comboStart, comboEnd));
    }
}