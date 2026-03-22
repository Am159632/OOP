import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUIManager<T> {
    private BorderPane rootPane;
    private SpaceVisualizer<T> currentVisualizer;
    private Space2DVisualizer<T> visualizer2D;
    private Space3DVisualizer<T> visualizer3D;
    private StackPane centerViewPane;
    private Map<String, AbstractCommandPanel<T>> panels;
    private boolean is3DMode = false;

    public AppUIManager(AbstractAnalyzableSpace<T> space, DistanceStrategy strategy, List<T> vocabulary) {
        rootPane = new BorderPane();
        visualizer2D = new Space2DVisualizer<>();
        visualizer3D = new Space3DVisualizer<>();
        currentVisualizer = visualizer2D;

        centerViewPane = new StackPane(currentVisualizer.getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");

        VBox sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(350);

        TextArea txtConsole = new TextArea("מערכת מוכנה...\n");
        txtConsole.setEditable(false);
        txtConsole.setWrapText(true);
        txtConsole.setPrefRowCount(8);

        panels = new HashMap<>();
        PcaCommandPanel<T> pcaPanel = new PcaCommandPanel<>(space, currentVisualizer, strategy, txtConsole::setText, vocabulary);
        panels.put("טען מרחב (PCA)", pcaPanel);

        ComboBox<String> actionBox = new ComboBox<>();
        actionBox.getItems().addAll(panels.keySet());
        actionBox.setValue("טען מרחב (PCA)");
        actionBox.setMaxWidth(Double.MAX_VALUE);

        VBox dynamicContainer = new VBox();
        dynamicContainer.getChildren().add(panels.get("טען מרחב (PCA)").getPanel());

        actionBox.setOnAction(e -> {
            dynamicContainer.getChildren().clear();
            dynamicContainer.getChildren().add(panels.get(actionBox.getValue()).getPanel());
        });

        visualizer2D.setOnNodeClicked(item -> panels.get(actionBox.getValue()).onNodeClicked(item));
        visualizer3D.setOnNodeClicked(item -> panels.get(actionBox.getValue()).onNodeClicked(item));

        ToggleButton btnToggleView = new ToggleButton("עבור לתצוגת 3D");
        btnToggleView.setMaxWidth(Double.MAX_VALUE);
        btnToggleView.getStyleClass().add("button");
        btnToggleView.setOnAction(e -> {
            is3DMode = btnToggleView.isSelected();
            currentVisualizer = is3DMode ? visualizer3D : visualizer2D;
            btnToggleView.setText(is3DMode ? "חזור לתצוגת 2D" : "עבור לתצוגת 3D");
            centerViewPane.getChildren().setAll(currentVisualizer.getVisualNode());

            for (AbstractCommandPanel<T> panel : panels.values()) {
                panel.onViewModeChanged(is3DMode, currentVisualizer);
            }
        });

        Button btnUndo = new Button("ביטול צבעים (Undo)");
        btnUndo.setMaxWidth(Double.MAX_VALUE);
        btnUndo.setOnAction(e -> currentVisualizer.clearHighlights());

        Label lblTitle = new Label("בחר פעולה:");
        lblTitle.getStyleClass().add("section-title");

        sideMenu.getChildren().addAll(btnToggleView, new Separator(), lblTitle, actionBox, dynamicContainer, new Separator(), btnUndo, new Separator(), txtConsole);

        rootPane.setCenter(centerViewPane);
        rootPane.setRight(sideMenu);

        pcaPanel.executeCommand();
    }

    public BorderPane getRoot() {
        return rootPane;
    }
}