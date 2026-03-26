import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUIManager<T> {
    private BorderPane rootPane;
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> currentVisualizer;
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

    public AppUIManager(AbstractAnalyzableSpace<T> space, DistanceStrategy defaultStrategy, List<T> vocabulary) {
        this.space = space;
        this.currentStrategy = defaultStrategy;
        this.rootPane = new BorderPane();

        visualizer2D = new Space2DVisualizer<>();
        visualizer3D = new Space3DVisualizer<>();
        currentVisualizer = visualizer2D;

        centerViewPane = new StackPane(currentVisualizer.getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");

        strategies = new HashMap<>();
        strategies.put("Euclidean", new EuclideanStrategy());
        strategies.put("Cosine", new CosineStrategy());

        // שינוי 1: הפקודות כבר לא מקבלות את הויזואליזר בבנאי!
        availableCommands = new ArrayList<>();
        availableCommands.add(new KnnCommand<>(space, vocabulary));
        availableCommands.add(new DistanceCommand<>(space, vocabulary));
        availableCommands.add(new AnalogyCommand<>(space, vocabulary));
        availableCommands.add(new CentroidCommand<>(space));
        availableCommands.add(new SemanticLineCommand<>(space, vocabulary));

        buildSideMenu();
        rootPane.setCenter(centerViewPane);
        executePca();

        visualizer2D.setOnNodeClicked(item -> { if (activeCommand != null) activeCommand.onNodeClicked(item); });
        visualizer3D.setOnNodeClicked(item -> { if (activeCommand != null) activeCommand.onNodeClicked(item); });
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
            currentVisualizer = is3DMode ? visualizer3D : visualizer2D;
            btnToggleView.setText(is3DMode ? "Switch to 2D View" : "Switch to 3D View");
            centerViewPane.getChildren().setAll(currentVisualizer.getVisualNode());
            pcaZ.setVisible(is3DMode);
            pcaZ.setManaged(is3DMode);
            executePca();
            // איזה יופי! אין פה יותר צורך לעדכן את הפקודה איזו גרפיקה פתוחה!
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
                // שינוי 2: הזרקת הויזואליזר הפעיל ישירות לפעולת ה-execute!
                txtConsole.setText(activeCommand.execute(currentVisualizer));
            }
        });

        box.getChildren().addAll(lblFunc, actionBox, dynamicInputsContainer, btnFunc);
        return box;
    }

    private VBox buildSettingsSection() {
        VBox box = new VBox(10);
        Label lblDist = new Label("3. Distance Metric");
        lblDist.getStyleClass().add("section-title");

        ComboBox<String> distanceBox = new ComboBox<>();
        distanceBox.getItems().addAll(strategies.keySet());
        distanceBox.setValue("Euclidean");
        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> {
            currentStrategy = strategies.get(distanceBox.getValue());
            if (activeCommand != null) activeCommand.setStrategy(currentStrategy);
        });

        Button btnUndo = new Button("Undo Command");
        btnUndo.setMaxWidth(Double.MAX_VALUE);
        btnUndo.setOnAction(e -> {
            if (activeCommand != null) {
                // שינוי 3: הזרקת הויזואליזר הפעיל גם לפעולת ה-undo!
                activeCommand.undo(currentVisualizer);
            } else {
                currentVisualizer.clearHighlights();
            }
            txtConsole.setText("");
        });

        box.getChildren().addAll(lblDist, distanceBox, btnUndo);
        return box;
    }

    private void executePca() {
        try {
            int x = Integer.parseInt(pcaX.getText());
            int y = Integer.parseInt(pcaY.getText());
            int z = is3DMode ? Integer.parseInt(pcaZ.getText()) : Integer.MIN_VALUE;
            currentVisualizer.clearHighlights();

            // שינוי 4 (אם גם PcaCommand עודכן): שולחים את הויזואליזר רק ל-execute
            // שים לב: אם לא עדכנת את PcaCommand, תחזיר אותו ל-(space, currentVisualizer, x, y, z) ואל תשלח ב-execute
            String res = new PcaCommand<>(space, x, y, z).execute(currentVisualizer);
            txtConsole.setText(res);
        } catch (Exception e) {
            txtConsole.setText("Error: Invalid PCA inputs.");
        }
    }

    public BorderPane getRoot() { return rootPane; }
}