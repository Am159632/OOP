import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SideMenuBuilder<T> {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;

    private TextField pcaX, pcaY, pcaZ;
    private ComboBox<String> actionBox;
    private TextArea txtConsole;
    private ComboBox<SpaceVisualizer<T>> viewSelector;

    public SideMenuBuilder(AppUIManager<T> uiManager, HistoryManager<T> history) {
        this.uiManager = uiManager;
        this.history = history;
    }

    public VBox build(TextArea console, ComboBox<SpaceVisualizer<T>> selector) {
        this.txtConsole = console;
        this.viewSelector = selector;

        VBox sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(350);

        sideMenu.getChildren().addAll(
                viewSelector, new Separator(),
                buildPcaSection(), new Separator(),
                buildFunctionsSection(), new Separator(),
                buildSettingsSection(), new Separator(),
                buildZoomSection(), txtConsole
        );
        return sideMenu;
    }

    private VBox buildPcaSection() {
        VBox box = new VBox(10);
        Label lblPca = new Label("1. Load PCA Space");
        lblPca.getStyleClass().add("section-title");

        pcaX = new TextField("0"); pcaX.setPromptText("X Axis");
        pcaY = new TextField("1"); pcaY.setPromptText("Y Axis");
        pcaZ = new TextField("2"); pcaZ.setPromptText("Z Axis");

        pcaZ.setVisible(false);
        pcaZ.setManaged(false);

        // כשמשנים תצוגה
        viewSelector.setOnAction(e -> {
            SpaceVisualizer<T> selected = viewSelector.getValue();
            uiManager.getCenterViewPane().getChildren().setAll(selected.getVisualNode());
            boolean is3D = selected.getClass().getSimpleName().contains("3D");

            pcaZ.setVisible(is3D);
            pcaZ.setManaged(is3D);

            // 1. שואלים את המנהל מה הערכים השמורים של התצוגה הזו
            String[] savedVals = uiManager.getSavedPcaValues(is3D);
            pcaX.setText(savedVals[0]);
            pcaY.setText(savedVals[1]);
            if (is3D) pcaZ.setText(savedVals[2]);

            // 2. מריצים בעזרת המנהל ומדפיסים בדיוק את מה שהוא עונה!
            String consoleOutput = uiManager.updatePcaLogic(pcaX.getText(), pcaY.getText(), pcaZ.getText(), is3D);
            txtConsole.setText(consoleOutput);
        });

        // כשלוחצים על הכפתור
        Button btnPca = new Button("Execute PCA");
        btnPca.setMaxWidth(Double.MAX_VALUE);
        btnPca.setOnAction(e -> {
            boolean is3D = pcaZ.isVisible();
            // מריצים בעזרת המנהל ומדפיסים בדיוק את מה שהוא עונה!
            String consoleOutput = uiManager.updatePcaLogic(pcaX.getText(), pcaY.getText(), pcaZ.getText(), is3D);
            txtConsole.setText(consoleOutput);
        });

        box.getChildren().addAll(lblPca, pcaX, pcaY, pcaZ, btnPca);
        return box;
    }

    private VBox buildFunctionsSection() {
        VBox box = new VBox(10);
        Label lblFunc = new Label("2. Analysis Functions");
        lblFunc.getStyleClass().add("section-title");

        actionBox = new ComboBox<>();
        uiManager.getAvailableCommands().forEach(cmd -> actionBox.getItems().add(cmd.getName()));
        if (!uiManager.getAvailableCommands().isEmpty()) {
            actionBox.setValue(uiManager.getAvailableCommands().get(0).getName());
        }
        actionBox.setMaxWidth(Double.MAX_VALUE);

        VBox dynamicInputs = new VBox();
        actionBox.setOnAction(e -> uiManager.updateActiveCommand(actionBox.getValue(), dynamicInputs));
        uiManager.updateActiveCommand(actionBox.getValue(), dynamicInputs);

        Button btnFunc = new Button("Execute Function");
        btnFunc.setMaxWidth(Double.MAX_VALUE);
        btnFunc.setOnAction(e -> {
            try {
                AppAction<T> action = uiManager.generateActiveAction();
                if (action != null) {
                    String res = action.execute();
                    txtConsole.setText(res); // מדפיס את הפלט של הפונקציה
                    history.addAction(action);
                }
            } catch (Exception ex) {
                txtConsole.setText("Error executing function.");
            }
        });

        box.getChildren().addAll(lblFunc, actionBox, dynamicInputs, btnFunc);
        return box;
    }

    private VBox buildSettingsSection() {
        VBox box = new VBox(10);
        Label lblDist = new Label("3. Settings & History");
        lblDist.getStyleClass().add("section-title");

        ComboBox<String> distanceBox = new ComboBox<>();
        distanceBox.getItems().addAll(uiManager.getStrategies().keySet());
        distanceBox.setValue("Euclidean");
        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> uiManager.setStrategy(uiManager.getStrategies().get(distanceBox.getValue())));

        HBox historyBox = new HBox(10);
        Button btnUndo = new Button("Undo"); btnUndo.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnUndo, Priority.ALWAYS);
        Button btnRedo = new Button("Redo"); btnRedo.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnRedo, Priority.ALWAYS);

        btnUndo.setOnAction(e -> {
            AppAction<T> action = history.undo();
            if (action != null) {
                AppAction<T> prev = history.peekUndo();
                txtConsole.setText(prev != null ? prev.execute() : "Reverted to clean space.");
                uiManager.getMultiVisualizer().clearHighlights();
            }
        });

        btnRedo.setOnAction(e -> {
            AppAction<T> action = history.redo();
            if (action != null) txtConsole.setText(action.execute());
        });

        historyBox.getChildren().addAll(btnUndo, btnRedo);
        box.getChildren().addAll(lblDist, distanceBox, historyBox);
        return box;
    }

    private VBox buildZoomSection() {
        VBox box = new VBox(10);
        Label lblZoom = new Label("4. Camera Zoom Level");
        lblZoom.getStyleClass().add("section-title");

        Slider zoomSlider = new Slider(1, 100, 50);
        zoomSlider.setShowTickMarks(true); zoomSlider.setShowTickLabels(true); zoomSlider.setMajorTickUnit(25);
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SpaceVisualizer<T> active = viewSelector.getValue();
            if (active != null) active.setZoom(newVal.doubleValue());
        });

        box.getChildren().addAll(lblZoom, zoomSlider);
        return box;
    }
}