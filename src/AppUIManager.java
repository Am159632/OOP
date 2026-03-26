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

        // מריצים PCA פעם אחת בלבד בהתחלה!
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

        ComboBox<SpaceVisualizer<T>> viewSelector = new ComboBox<>();
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
            // הסרנו את הקריאה ל-executePca() מכאן כדי לא למחוק צבעים בהחלפת מסך!
        });

        VBox pcaSection = buildPcaSection();
        VBox funcSection = buildFunctionsSection();
        VBox settingsSection = buildSettingsSection();

        sideMenu.getChildren().addAll(viewSelector, new Separator(), pcaSection, new Separator(), funcSection, new Separator(), settingsSection, txtConsole);
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

        Button btnUndo = new Button("Undo");
        btnUndo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnUndo, Priority.ALWAYS);
        btnUndo.setOnAction(e -> {
            if (!undoStack.isEmpty()) {
                // 1. מוציאים את הפקודה האחרונה ומבטלים אותה
                SpaceCommand<T> cmd = undoStack.pop();
                cmd.undo(multiVisualizer);
                redoStack.push(cmd);

                // 2. בודקים אם יש פקודה קודמת להציג
                if (!undoStack.isEmpty()) {
                    SpaceCommand<T> prevCmd = undoStack.peek();
                    actionBox.setValue(prevCmd.getName()); // מחזיר את הקומבו לפונקציה הקודמת!
                    txtConsole.setText(prevCmd.execute(multiVisualizer)); // מריץ אותה מחדש בשקט כדי לצבוע
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
                SpaceCommand<T> cmd = redoStack.pop();
                actionBox.setValue(cmd.getName()); // מחליף קומבו לפונקציה המשוחזרת
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