package ui;

import visuals.SpaceVisualizer;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PcaSection<T> implements MenuSection {
    private AppUIManager<T> uiManager;
    private ComboBox<SpaceVisualizer<T>> viewSelector;
    private TextArea txtConsole;
    private TextField pcaX, pcaY, pcaZ;

    public PcaSection(AppUIManager<T> uiManager, ComboBox<SpaceVisualizer<T>> viewSelector, TextArea txtConsole) {
        this.uiManager = uiManager;
        this.viewSelector = viewSelector;
        this.txtConsole = txtConsole;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblPca = new Label("Load PCA Space");
        lblPca.getStyleClass().add("section-title");

        pcaX = new TextField("0"); pcaX.setPromptText("X Axis");
        pcaY = new TextField("1"); pcaY.setPromptText("Y Axis");
        pcaZ = new TextField("2"); pcaZ.setPromptText("Z Axis");

        pcaZ.setVisible(false);
        pcaZ.setManaged(false);

        HBox pcaInputs = new HBox(5, pcaX, pcaY, pcaZ);
        HBox.setHgrow(pcaX, Priority.ALWAYS);
        HBox.setHgrow(pcaY, Priority.ALWAYS);
        HBox.setHgrow(pcaZ, Priority.ALWAYS);

        viewSelector.setOnAction(e -> {
            SpaceVisualizer<T> selected = viewSelector.getValue();
            uiManager.getCenterViewPane().getChildren().setAll(selected.getVisualNode());
            boolean is3D = selected.getClass().getSimpleName().contains("3D");

            pcaZ.setVisible(is3D);
            pcaZ.setManaged(is3D);

            String[] savedVals = uiManager.getSavedPcaValues(is3D);
            pcaX.setText(savedVals[0]);
            pcaY.setText(savedVals[1]);
            if (is3D) pcaZ.setText(savedVals[2]);

            String consoleOutput = uiManager.updatePcaLogic(pcaX.getText(), pcaY.getText(), pcaZ.getText(), is3D);
            txtConsole.setText(consoleOutput);
        });

        Button btnPca = new Button("Execute PCA");
        btnPca.setMaxWidth(Double.MAX_VALUE);
        btnPca.setOnAction(e -> {
            boolean is3D = pcaZ.isVisible();
            String consoleOutput = uiManager.updatePcaLogic(pcaX.getText(), pcaY.getText(), pcaZ.getText(), is3D);
            txtConsole.setText(consoleOutput);
        });

        box.getChildren().addAll(lblPca, pcaInputs, btnPca);
        return box;
    }
}