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
    private boolean is3DMode = false;

    private TextField pcaX, pcaY, pcaZ;
    private Map<String, DistanceStrategy> strategies;
    private DistanceStrategy currentStrategy;

    private List<SpaceCommand<T>> availableCommands;
    private SpaceCommand<T> activeCommand;

    private Stack<SpaceCommand<T>> undoStack = new Stack<>();
    private Stack<SpaceCommand<T>> redoStack = new Stack<>();

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

        strategies = new HashMap<>();
        strategies.put("Euclidean", new EuclideanStrategy());
        strategies.put("Cosine", new CosineStrategy());

        availableCommands = new ArrayList<>();
        availableCommands.add(new KnnCommand<>(space, vocabulary));
        availableCommands.add(new DistanceCommand<>(space, vocabulary));
        availableCommands.add(new AnalogyCommand<>(space, vocabulary));
        availableCommands.add(new CentroidCommand<>(space));
        availableCommands.add(new SemanticLineCommand<>(space, vocabulary));

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

        ToggleButton btnToggleView = new ToggleButton("Switch to 3D View");
        btnToggleView.setMaxWidth(Double.MAX_VALUE);
        btnToggleView.getStyleClass().add("button");
        btnToggleView.setOnAction(e -> {
            is3DMode = btnToggleView.isSelected();
            SpaceVisualizer<T> activeVis = is3DMode ? visualizer3D : visualizer2D;
            btnToggleView.setText(is3DMode ? "Switch to 2D View" : "Switch to 3D View");
            centerViewPane.getChildren().setAll(activeVis.getVisualNode());
            pcaZ.setVisible(is3DMode);
            pcaZ.setManaged(is3DMode);
            executePca();
        });

        VBox pcaSection = buildPcaSection();
        VBox funcSection = buildFunctionsSection();
        VBox settingsSection = buildSettingsSection();

        sideMenu.getChildren().addAll(btnToggleView, new Separator(), pcaSection, new Separator(), funcSection, new Separator(), settingsSection, txtConsole);
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

        ComboBox<String> actionBox = new ComboBox<>();
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
                txtConsole.setText(activeCommand.execute(multiVisualizer));
                undoStack.push(activeCommand);
                redoStack.clear();
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

        Button btnUndo = new Button("Undo Command");
        btnUndo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnUndo, Priority.ALWAYS);
        btnUndo.setOnAction(e -> {
            if (!undoStack.isEmpty()) {
                SpaceCommand<T> cmd = undoStack.pop();
                cmd.undo(multiVisualizer);
                txtConsole.setText("");
                redoStack.push(cmd);
            } else {
                multiVisualizer.clearHighlights();
                txtConsole.setText("");
            }
        });

        Button btnRedo = new Button("Redo Command");
        btnRedo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRedo, Priority.ALWAYS);
        btnRedo.setOnAction(e -> {
            if (!redoStack.isEmpty()) {
                SpaceCommand<T> cmd = redoStack.pop();
                txtConsole.setText(cmd.execute(multiVisualizer));
                undoStack.push(cmd);
            }
        });

        historyBox.getChildren().addAll(btnUndo, btnRedo);
        box.getChildren().addAll(lblDist, distanceBox, historyBox);
        return box;
    }

    private void executePca() {
        try {
            int x = Integer.parseInt(pcaX.getText());
            int y = Integer.parseInt(pcaY.getText());
            int z = is3DMode ? Integer.parseInt(pcaZ.getText()) : Integer.MIN_VALUE;
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