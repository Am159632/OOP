package ui;

import core.*;
import math.*;
import actions.*;
import visuals.*;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;

public class SemanticLineUI<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboStart, comboEnd;
    private TextField txtK;

    public SemanticLineUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboStart = UIUtils.createSearchableComboBox(vocabulary);
        comboStart.setPromptText("Start Item");
        Button btnClearStart = new Button("X"); btnClearStart.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClearStart.setOnAction(e -> comboStart.getEditor().clear());
        HBox row1 = new HBox(5, comboStart, btnClearStart); HBox.setHgrow(comboStart, Priority.ALWAYS);

        comboEnd = UIUtils.createSearchableComboBox(vocabulary);
        comboEnd.setPromptText("End Item");
        Button btnClearEnd = new Button("X"); btnClearEnd.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClearEnd.setOnAction(e -> comboEnd.getEditor().clear());
        HBox row2 = new HBox(5, comboEnd, btnClearEnd); HBox.setHgrow(comboEnd, Priority.ALWAYS);

        txtK = new TextField(); txtK.setPromptText("Amount of Steps (K)");

        uiContainer.getChildren().addAll(row1, row2, txtK);
    }

    @Override
    public String getName() { return "Semantic Line"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String ts = comboStart.getEditor().getText(); T start = (ts != null && !ts.isEmpty()) ? (T) ts : comboStart.getValue();
        String te = comboEnd.getEditor().getText(); T end = (te != null && !te.isEmpty()) ? (T) te : comboEnd.getValue();

        if (start == null || end == null) throw new IllegalArgumentException("Empty Inputs");
        int k = Integer.parseInt(txtK.getText());

        return new SemanticLineAction<>(space, visualizer, strategy, start, end, k);
    }

    @Override
    public void onNodeClicked(T item) {
        if (comboStart.getEditor().getText().isEmpty()) {
            comboStart.getEditor().setText(item.toString());
        } else if (comboEnd.getEditor().getText().isEmpty()) {
            comboEnd.getEditor().setText(item.toString());
        }
    }
}