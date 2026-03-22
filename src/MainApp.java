import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainApp extends Application {
    private OurSpace ourSpace;
    private SpaceVisualizer<String> currentVisualizer;
    private Space2DVisualizer visualizer2D;
    private Space3DVisualizer visualizer3D;
    private DistanceStrategy strategy;
    private TextArea txtConsole;
    private StackPane centerViewPane;

    private VBox dynamicInputs;
    private ComboBox<String> in1, in2, in3;
    private boolean is3DMode = false;
    private List<String> vocabulary;

    @Override
    public void init() {
        try {
            ourSpace = new OurSpace();
            strategy = new EuclideanStrategy();
            ourSpace.loadFiles("C:/Users/asafm/IdeaProjects/OOP/full_vectors.json", "C:/Users/asafm/IdeaProjects/OOP/pca_vectors.json");
            vocabulary = new java.util.ArrayList<>(ourSpace.getItems("FULL"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        visualizer2D = new Space2DVisualizer();
        visualizer3D = new Space3DVisualizer();
        currentVisualizer = visualizer2D;

        setupVisualizerClick(visualizer2D);
        setupVisualizerClick(visualizer3D);

        centerViewPane = new StackPane(currentVisualizer.getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");

        VBox sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(350);

        txtConsole = new TextArea("המערכת מוכנה...\n");
        txtConsole.setEditable(false);
        txtConsole.setWrapText(true);
        txtConsole.setPrefRowCount(8);

        ComboBox<String> actionBox = new ComboBox<>();
        actionBox.getItems().addAll("טען מרחב (PCA)", "מצא שכנים (KNN)", "חשב מרחק", "ציר סמנטי", "אנלוגיה", "מרכז מסה (ממוצע)");
        actionBox.setValue("טען מרחב (PCA)");
        actionBox.setMaxWidth(Double.MAX_VALUE);

        dynamicInputs = new VBox(10);
        in1 = createSearchableComboBox();
        in2 = createSearchableComboBox();
        in3 = createSearchableComboBox();

        ToggleButton btnToggleView = new ToggleButton("עבור לתצוגת 3D");
        btnToggleView.setMaxWidth(Double.MAX_VALUE);
        btnToggleView.getStyleClass().add("button");
        btnToggleView.setOnAction(e -> {
            is3DMode = btnToggleView.isSelected();
            currentVisualizer = is3DMode ? visualizer3D : visualizer2D;
            btnToggleView.setText(is3DMode ? "חזור לתצוגת 2D" : "עבור לתצוגת 3D");
            centerViewPane.getChildren().setAll(currentVisualizer.getVisualNode());
            updateDynamicInputs(actionBox.getValue());
        });

        actionBox.setOnAction(e -> updateDynamicInputs(actionBox.getValue()));
        updateDynamicInputs(actionBox.getValue());

        Button btnExecute = new Button("בצע פעולה");
        btnExecute.setMaxWidth(Double.MAX_VALUE);
        btnExecute.setOnAction(e -> executeCommand(actionBox.getValue()));

        Button btnUndo = new Button("ביטול צבעים (Undo)");
        btnUndo.setMaxWidth(Double.MAX_VALUE);
        btnUndo.setOnAction(e -> currentVisualizer.clearHighlights());

        Label lblTitle = new Label("בחר פעולה:");
        lblTitle.getStyleClass().add("section-title");

        sideMenu.getChildren().addAll(btnToggleView, new Separator(), lblTitle, actionBox, dynamicInputs, btnExecute, new Separator(), btnUndo, new Separator(), txtConsole);

        BorderPane root = new BorderPane();
        root.setCenter(centerViewPane);
        root.setRight(sideMenu);
        Scene scene = new Scene(root, 1200, 750);

        try {
            java.net.URL cssUrl = getClass().getResource("/style.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        } catch(Exception ignored) {}

        primaryStage.setScene(scene);
        primaryStage.setTitle("Word Embedding Visualizer");
        primaryStage.show();
    }

    private void setupVisualizerClick(SpaceVisualizer<String> visualizer) {
        visualizer.setOnNodeClicked(word -> {
            if (in1.isVisible() && (in1.getEditor().getText() == null || in1.getEditor().getText().isEmpty())) in1.getEditor().setText(word);
            else if (in2.isVisible() && (in2.getEditor().getText() == null || in2.getEditor().getText().isEmpty())) in2.getEditor().setText(word);
            else if (in3.isVisible() && (in3.getEditor().getText() == null || in3.getEditor().getText().isEmpty())) in3.getEditor().setText(word);
        });
    }

    private ComboBox<String> createSearchableComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setEditable(true);
        comboBox.setMaxWidth(Double.MAX_VALUE);

        if (vocabulary != null) {
            comboBox.getItems().addAll(vocabulary);
        }

        comboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                comboBox.setItems(FXCollections.observableArrayList(vocabulary));
            } else {
                List<String> filtered = vocabulary.stream()
                        .filter(word -> word.toLowerCase().startsWith(newText.toLowerCase()))
                        .collect(Collectors.toList());
                comboBox.setItems(FXCollections.observableArrayList(filtered));
            }
        });
        return comboBox;
    }

    private void updateDynamicInputs(String action) {
        dynamicInputs.getChildren().clear();
        in1.getEditor().clear();
        in2.getEditor().clear();
        in3.getEditor().clear();
        in1.setVisible(true);
        in2.setVisible(true);
        in3.setVisible(true);

        switch (action) {
            case "טען מרחב (PCA)":
                in1.setPromptText("ציר X (למשל: 0)");
                in1.getEditor().setText("0");
                in2.setPromptText("ציר Y (למשל: 1)");
                in2.getEditor().setText("1");
                dynamicInputs.getChildren().addAll(in1, in2);
                if (is3DMode) {
                    in3.setPromptText("ציר Z (למשל: 2)");
                    in3.getEditor().setText("2");
                    dynamicInputs.getChildren().add(in3);
                }
                break;
            case "מצא שכנים (KNN)":
                in1.setPromptText("מילת מטרה (למשל: king)");
                in2.setPromptText("כמות שכנים K");
                in2.getEditor().setText("5");
                dynamicInputs.getChildren().addAll(in1, in2);
                break;
            case "חשב מרחק":
                in1.setPromptText("מילה 1");
                in2.setPromptText("מילה 2");
                dynamicInputs.getChildren().addAll(in1, in2);
                break;
            case "ציר סמנטי":
                in1.setPromptText("התחלה (bad)");
                in2.setPromptText("סיום (good)");
                in3.setPromptText("כמות תוצאות");
                in3.getEditor().setText("5");
                dynamicInputs.getChildren().addAll(in1, in2, in3);
                break;
            case "אנלוגיה":
                in1.setPromptText("מילה 1 (king)");
                in2.setPromptText("מילה 2 (man)");
                in3.setPromptText("מילה 3 (woman)");
                dynamicInputs.getChildren().addAll(in1, in2, in3);
                break;
            case "מרכז מסה (ממוצע)":
                in1.setPromptText("הכנס מילים מופרדות בפסיקים");
                dynamicInputs.getChildren().add(in1);
                break;
        }
    }

    private void executeCommand(String action) {
        try {
            currentVisualizer.clearHighlights();
            String res = "";

            String val1 = in1.getEditor().getText();
            String val2 = in2.getEditor().getText();
            String val3 = in3.getEditor().getText();

            switch (action) {
                case "טען מרחב (PCA)":
                    int x = Integer.parseInt(val1);
                    int y = Integer.parseInt(val2);
                    int z = is3DMode ? Integer.parseInt(val3) : 0;
                    res = new PcaCommand<>(ourSpace, currentVisualizer, x, y, z).execute();
                    break;
                case "מצא שכנים (KNN)":
                    res = new KnnCommand<>(ourSpace, currentVisualizer, strategy, val1, Integer.parseInt(val2)).execute();
                    break;
                case "חשב מרחק":
                    res = new DistanceCommand<>(ourSpace, currentVisualizer, strategy, val1, val2).execute();
                    break;
                case "ציר סמנטי":
                    res = new SemanticLineCommand<>(ourSpace, currentVisualizer, strategy, val1, val2, Integer.parseInt(val3)).execute();
                    break;
                case "אנלוגיה":
                    res = new AnalogyCommand<>(ourSpace, currentVisualizer, strategy, val1, val2, val3).execute();
                    break;
                case "מרכז מסה (ממוצע)":
                    List<String> group = Arrays.asList(val1.split("\\s*,\\s*"));
                    res = new CentroidCommand<>(ourSpace, currentVisualizer, strategy, group).execute();
                    break;
            }
            txtConsole.setText(res);
        } catch (Exception e) {
            e.printStackTrace();
            txtConsole.setText("שגיאה בביצוע הפעולה.");
        }
    }

    public static void main(String[] args) { launch(args); }
}