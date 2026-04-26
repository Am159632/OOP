package ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class PcaTopBar<T> {
    private AppUIManager<T> uiManager;
    private TextArea txtConsole;
    private HBox pcaInputsBox;
    private List<ComboBox<Integer>> currentCombos;
    private HBox mainNode;

    public PcaTopBar(AppUIManager<T> uiManager, TextArea txtConsole) {
        this.uiManager = uiManager;
        this.txtConsole = txtConsole;
        this.pcaInputsBox = new HBox(5);
        this.pcaInputsBox.setAlignment(Pos.CENTER_LEFT);
        this.currentCombos = new ArrayList<>();

        Button btnPca = new Button("Execute PCA");
        btnPca.setOnAction(e -> executePca());

        Label lbl = new Label("  PCA Axes: ");

        this.mainNode = new HBox(10);
        this.mainNode.setAlignment(Pos.CENTER_LEFT);
        this.mainNode.getChildren().addAll(lbl, pcaInputsBox, btnPca);
    }

    public HBox build() {
        return mainNode;
    }

    public void updateDimensions(int dimensions) {
        pcaInputsBox.getChildren().clear();
        currentCombos.clear();

        int maxDim = 0;
        try {
            Set<T> items = uiManager.getSpace().getItems("PCA");
            if (items != null && !items.isEmpty()) {
                double[] vec = uiManager.getSpace().getVector("PCA", items.iterator().next());
                maxDim = (vec != null) ? vec.length : 10;
            }
        } catch (Exception e) {
            maxDim = 50;
        }

        List<Integer> options = IntStream.range(0, maxDim).boxed().collect(Collectors.toList());
        String[] savedVals = uiManager.getSavedPcaValues(dimensions);

        for (int i = 0; i < dimensions; i++) {
            ComboBox<Integer> cb = new ComboBox<>();
            cb.getItems().addAll(options);

            int initialVal = i;
            if (i < savedVals.length) {
                try {
                    initialVal = Integer.parseInt(savedVals[i]);
                } catch (Exception e) {
                }
            }
            cb.setValue(initialVal % Math.max(1, maxDim));
            cb.setPrefWidth(60);

            currentCombos.add(cb);
            pcaInputsBox.getChildren().add(cb);
        }
    }

    public void executePca() {
        try {
            String[] axes = currentCombos.stream()
                    .map(cb -> String.valueOf(cb.getValue()))
                    .toArray(String[]::new);
            String consoleOutput = uiManager.updatePcaLogic(axes);
            txtConsole.setText(consoleOutput);
        } catch (Exception e) {
            txtConsole.setText("Error: Could not execute PCA. Please check data consistency.");
        }
    }
}