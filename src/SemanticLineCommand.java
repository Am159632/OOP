import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticLineCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboStart, comboEnd;
    private TextField txtK;

    public SemanticLineCommand(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboStart = createSearchableComboBox(); comboStart.setPromptText("Start Item");
        Button btnClearStart = new Button("X"); btnClearStart.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClearStart.setOnAction(e -> comboStart.getEditor().clear());
        HBox row1 = new HBox(5, comboStart, btnClearStart); HBox.setHgrow(comboStart, Priority.ALWAYS);

        comboEnd = createSearchableComboBox(); comboEnd.setPromptText("End Item");
        Button btnClearEnd = new Button("X"); btnClearEnd.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClearEnd.setOnAction(e -> comboEnd.getEditor().clear());
        HBox row2 = new HBox(5, comboEnd, btnClearEnd); HBox.setHgrow(comboEnd, Priority.ALWAYS);

        txtK = new TextField("5"); txtK.setPromptText("Amount of Steps (K)");

        uiContainer.getChildren().addAll(row1, row2, txtK);
    }

    private ComboBox<T> createSearchableComboBox() {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setEditable(true);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        if (vocabulary != null) comboBox.getItems().addAll(vocabulary);

        comboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                comboBox.setItems(FXCollections.observableArrayList(vocabulary));
            } else {
                List<T> filtered = vocabulary.stream()
                        .filter(item -> item.toString().toLowerCase().startsWith(newText.toLowerCase()))
                        .collect(Collectors.toList());
                comboBox.setItems(FXCollections.observableArrayList(filtered));
            }
        });
        return comboBox;
    }

    @Override
    public String getName() { return "Semantic Line"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @Override
    public String execute(SpaceVisualizer<T> visualizer) {
        if (strategy == null) return "Error: Distance strategy not set.";
        try {
            T startId = comboStart.getEditor().getText().isEmpty() ? comboStart.getValue() : (T) comboStart.getEditor().getText();
            T endId = comboEnd.getEditor().getText().isEmpty() ? comboEnd.getValue() : (T) comboEnd.getEditor().getText();
            int k = Integer.parseInt(txtK.getText());

            List<ItemDistance<T>> projections = new ArrayList<>();
            for (T item : space.getItems("FULL")) {
                if (item.equals(startId) || item.equals(endId)) continue;
                ProjectionFunction<T> func = new ProjectionFunction<>("FULL", item, startId, endId);
                double val = space.executeFunction(func, strategy);
                projections.add(new ItemDistance<>(item, val));
            }

            projections.sort(Comparator.comparingDouble(ItemDistance::getDistance));

            List<T> closeToStart = projections.stream().limit(k).map(ItemDistance::getId).collect(Collectors.toList());
            List<T> closeToEnd = projections.stream().skip(Math.max(0, projections.size() - k)).map(ItemDistance::getId).collect(Collectors.toList());
            java.util.Collections.reverse(closeToEnd);

            visualizer.clearHighlights();
            visualizer.highlightItems(closeToStart, "#FF4500");
            visualizer.highlightItems(closeToEnd, "#32CD32");

            StringBuilder sb = new StringBuilder();
            sb.append("Semantic Line: ").append(startId).append(" <--> ").append(endId).append("\n\n");
            sb.append("Top ").append(k).append(" closest to '").append(startId).append("':\n");
            closeToStart.forEach(word -> sb.append("- ").append(word).append("\n"));
            sb.append("\nTop ").append(k).append(" closest to '").append(endId).append("':\n");
            closeToEnd.forEach(word -> sb.append("- ").append(word).append("\n"));

            return sb.toString();
        } catch (Exception e) {
            return "Error executing Semantic Line. Check inputs.";
        }
    }

    @Override
    public void undo(SpaceVisualizer<T> visualizer) { visualizer.clearHighlights(); }

    @Override
    public void onNodeClicked(T item) {
        if (comboStart.getEditor().getText().isEmpty()) {
            comboStart.getEditor().setText(item.toString());
        } else if (comboEnd.getEditor().getText().isEmpty()) {
            comboEnd.getEditor().setText(item.toString());
        }
    }

}