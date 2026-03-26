import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AppUIManager<T> {
    private BorderPane rootPane;
    private AbstractAnalyzableSpace<T> space;
    private MultiSpaceVisualizer<T> multiVisualizer;
    private Space2DVisualizer<T> visualizer2D;
    private Space3DVisualizer<T> visualizer3D;
    private StackPane centerViewPane;
    private TextArea txtConsole;
    private ComboBox<String> actionBox;
    private Slider zoomSlider;

    private TextField pcaX, pcaY, pcaZ;
    private Map<String, DistanceStrategy> strategies;
    private DistanceStrategy currentStrategy;

    private List<SpaceCommand<T>> availableCommands;
    private SpaceCommand<T> activeCommand;
    private ComboBox<SpaceVisualizer<T>> viewSelector;

    private Stack<AppAction<T>> undoStack = new Stack<>();
    private Stack<AppAction<T>> redoStack = new Stack<>();

    public AppUIManager(AbstractAnalyzableSpace<T> space, DistanceStrategy defaultStrategy, List<T> vocabulary) {
        this.space = space;
        this.currentStrategy = defaultStrategy;
        this.rootPane = new BorderPane();

        visualizer2D = new Space2DVisualizer<>();
        visualizer3D = new Space3DVisualizer<>();

        List<SpaceVisualizer<T>> allVisualizers = List.of(visualizer2D, visualizer3D);
        multiVisualizer = new MultiSpaceVisualizer<>(allVisualizers);

        centerViewPane = new StackPane(visualizer2D.getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");

        centerViewPane.setOnScroll(event -> {
            if (zoomSlider != null) {
                double speed = 5.0;
                if (event.getDeltaY() > 0) {
                    zoomSlider.setValue(zoomSlider.getValue() + speed);
                } else {
                    zoomSlider.setValue(zoomSlider.getValue() - speed);
                }
            }
        });

        strategies = new HashMap<>();
        strategies.put("Euclidean", new EuclideanStrategy());
        strategies.put("Cosine", new CosineStrategy());

        availableCommands = new ArrayList<>();
        availableCommands.add(new KnnUI<>(space, vocabulary));
        availableCommands.add(new DistanceUI<>(space, vocabulary));
        availableCommands.add(new AnalogyUI<>(space, vocabulary));
        availableCommands.add(new CentroidUI<>(space));
        availableCommands.add(new SemanticLineUI<>(space, vocabulary));

        buildSideMenu();
        rootPane.setCenter(centerViewPane);
        executePca();

        multiVisualizer.setOnNodeClicked(item -> {
            if (activeCommand != null) {
                activeCommand.onNodeClicked(item);
            }
        });
    }

    private void buildSideMenu() {
        VBox sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(350);

        txtConsole = new TextArea("System Ready...\n");
        txtConsole.setEditable(false);
        txtConsole.setWrapText(true);
        txtConsole.setPrefRowCount(8);

        viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll(visualizer2D, visualizer3D);
        viewSelector.setValue(visualizer2D);
        viewSelector.setMaxWidth(Double.MAX_VALUE);
        viewSelector.setStyle("-fx-font-weight: bold;");

        viewSelector.setOnAction(e -> {
            SpaceVisualizer<T> selected = viewSelector.getValue();
            centerViewPane.getChildren().setAll(selected.getVisualNode());
            boolean is3D = (selected instanceof Space3DVisualizer);
            pcaZ.setVisible(is3D);
            pcaZ.setManaged(is3D);
        });

        VBox pcaSection = buildPcaSection();
        VBox funcSection = buildFunctionsSection();
        VBox settingsSection = buildSettingsSection();
        VBox zoomSection = buildZoomSection();

        sideMenu.getChildren().addAll(viewSelector, new Separator(), pcaSection, new Separator(), funcSection, new Separator(), settingsSection, new Separator(), zoomSection, txtConsole);
        rootPane.setRight(sideMenu);
    }

    private VBox buildPcaSection() {
        VBox box = new VBox(10);
        Label lblPca = new Label("1. Load PCA Space");
        lblPca.getStyleClass().add("section-title");

        pcaX = new TextField("0"); pcaX.setPromptText("X Axis");
        pcaY = new TextField("1"); pcaY.setPromptText("Y Axis");
        pcaZ = new TextField("2"); pcaZ.setPromptText("Z Axis");
        pcaZ.setVisible(false); pcaZ.setManaged(false);

        Button btnPca = new Button("Execute PCA");
        btnPca.setMaxWidth(Double.MAX_VALUE);
        btnPca.setOnAction(e -> executePca());

        box.getChildren().addAll(lblPca, pcaX, pcaY, pcaZ, btnPca);
        return box;
    }

    private VBox buildFunctionsSection() {
        VBox box = new VBox(10);
        Label lblFunc = new Label("2. Analysis Functions");
        lblFunc.getStyleClass().add("section-title");

        actionBox = new ComboBox<>();
        for (SpaceCommand<T> cmd : availableCommands) {
            actionBox.getItems().add(cmd.getName());
        }
        if (!availableCommands.isEmpty()) actionBox.setValue(availableCommands.get(0).getName());
        actionBox.setMaxWidth(Double.MAX_VALUE);

        VBox dynamicInputsContainer = new VBox();

        Runnable updateActiveCommand = () -> {
            dynamicInputsContainer.getChildren().clear();
            String selected = actionBox.getValue();
            for (SpaceCommand<T> cmd : availableCommands) {
                if (cmd.getName().equals(selected)) {
                    activeCommand = cmd;
                    activeCommand.setStrategy(currentStrategy);
                    dynamicInputsContainer.getChildren().add(activeCommand.getUI());
                    break;
                }
            }
        };

        actionBox.setOnAction(e -> updateActiveCommand.run());
        updateActiveCommand.run();

        Button btnFunc = new Button("Execute Function");
        btnFunc.setMaxWidth(Double.MAX_VALUE);
        btnFunc.setOnAction(e -> {
            if (activeCommand != null) {
                try {
                    AppAction<T> action = activeCommand.generateAction(multiVisualizer);
                    String res = action.execute();
                    txtConsole.setText(res);
                    undoStack.push(action);
                    redoStack.clear();
                } catch (Exception ex) {
                    txtConsole.setText("Error: Missing or invalid inputs.");
                }
            }
        });

        box.getChildren().addAll(lblFunc, actionBox, dynamicInputsContainer, btnFunc);
        return box;
    }

    private VBox buildSettingsSection() {
        VBox box = new VBox(10);
        Label lblDist = new Label("3. Settings & History");
        lblDist.getStyleClass().add("section-title");

        ComboBox<String> distanceBox = new ComboBox<>();
        distanceBox.getItems().addAll(strategies.keySet());
        distanceBox.setValue("Euclidean");
        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> {
            currentStrategy = strategies.get(distanceBox.getValue());
            if (activeCommand != null) activeCommand.setStrategy(currentStrategy);
        });

        HBox historyBox = new HBox(10);

        Button btnUndo = new Button("Undo");
        btnUndo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnUndo, Priority.ALWAYS);
        btnUndo.setOnAction(e -> {
            if (!undoStack.isEmpty()) {
                AppAction<T> action = undoStack.pop();
                action.undo();
                redoStack.push(action);

                if (!undoStack.isEmpty()) {
                    AppAction<T> prevAction = undoStack.peek();
                    actionBox.setValue(prevAction.getName());
                    txtConsole.setText(prevAction.execute());
                } else {
                    multiVisualizer.clearHighlights();
                    txtConsole.setText("Reverted to clean space.");
                }
            }
        });

        Button btnRedo = new Button("Redo");
        btnRedo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRedo, Priority.ALWAYS);
        btnRedo.setOnAction(e -> {
            if (!redoStack.isEmpty()) {
                AppAction<T> action = redoStack.pop();
                actionBox.setValue(action.getName());
                txtConsole.setText(action.execute());
                undoStack.push(action);
            }
        });

        historyBox.getChildren().addAll(btnUndo, btnRedo);
        box.getChildren().addAll(lblDist, distanceBox, historyBox);
        return box;
    }

    private VBox buildZoomSection() {
        VBox box = new VBox(10);
        Label lblZoom = new Label("4. Camera Zoom Level");
        lblZoom.getStyleClass().add("section-title");

        zoomSlider = new Slider(1, 100, 50);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setMajorTickUnit(25);

        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SpaceVisualizer<T> active = viewSelector.getValue();
            if (active != null) {
                active.setZoom(newVal.doubleValue());
            }
        });

        box.getChildren().addAll(lblZoom, zoomSlider);
        return box;
    }

    private void executePca() {
        try {
            int x = Integer.parseInt(pcaX.getText());
            int y = Integer.parseInt(pcaY.getText());
            int z = Integer.parseInt(pcaZ.getText());

            multiVisualizer.clearHighlights();
            String res = new PcaCommand<>(space, x, y, z).execute(multiVisualizer);
            txtConsole.setText(res);
            undoStack.clear();
            redoStack.clear();
        } catch (Exception e) {
            txtConsole.setText("Error: Invalid PCA inputs.");
        }
    }

    public BorderPane getRoot() { return rootPane; }
}