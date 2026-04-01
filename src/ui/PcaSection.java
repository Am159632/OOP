package ui;

import visuals.GUIVisualizer;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;

public class PcaSection<T> implements MenuSection {
    private AppUIManager<T> uiManager;
    private ComboBox<GUIVisualizer<T>> viewSelector;
    private TextArea txtConsole;

    private HBox pcaInputsBox;
    private List<TextField> currentFields;

    public PcaSection(AppUIManager<T> uiManager, ComboBox<GUIVisualizer<T>> viewSelector, TextArea txtConsole) {
        this.uiManager = uiManager;
        this.viewSelector = viewSelector;
        this.txtConsole = txtConsole;
        this.pcaInputsBox = new HBox(5);
        this.currentFields = new ArrayList<>();
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblPca = new Label("Load PCA Space");
        lblPca.getStyleClass().add("section-title");

        Button btnPca = new Button("Execute PCA");
        btnPca.setMaxWidth(Double.MAX_VALUE);

        viewSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                uiManager.getCenterViewPane().getChildren().setAll(newVal.getVisualNode());
                rebuildInputFields(newVal.getDimensions());
                executePca();
            }
        });

        btnPca.setOnAction(e -> executePca());

        box.getChildren().addAll(lblPca, pcaInputsBox, btnPca);
        GUIVisualizer<T> initialView = viewSelector.getValue();
        if (initialView != null) {
            rebuildInputFields(initialView.getDimensions());
        }
        return box;
    }

    private void rebuildInputFields(int dimensions) {
        pcaInputsBox.getChildren().clear();
        currentFields.clear();

        String[] savedVals = uiManager.getSavedPcaValues(dimensions);
        String[] axisLabels = {"X Axis", "Y Axis", "Z Axis", "W Axis"};

        for (int i = 0; i < dimensions; i++) {
            TextField tf = new TextField(savedVals[i]);
            tf.setPromptText(axisLabels[i]);
            HBox.setHgrow(tf, Priority.ALWAYS);

            currentFields.add(tf);
            pcaInputsBox.getChildren().add(tf);
        }
    }

    private void executePca() {
        String[] axes = currentFields.stream().map(TextField::getText).toArray(String[]::new);
        String consoleOutput = uiManager.updatePcaLogic(axes);
        txtConsole.setText(consoleOutput);
    }
}