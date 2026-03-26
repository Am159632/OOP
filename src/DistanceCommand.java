import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.stream.Collectors;

public class DistanceCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboW1, comboW2;

    public DistanceCommand(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboW1 = createSearchableComboBox();
        comboW1.setPromptText("Item 1");
        Button btnClear1 = new Button("X");
        btnClear1.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear1.setOnAction(e -> comboW1.getEditor().clear());
        HBox row1 = new HBox(5, comboW1, btnClear1);
        HBox.setHgrow(comboW1, Priority.ALWAYS);

        comboW2 = createSearchableComboBox();
        comboW2.setPromptText("Item 2");
        Button btnClear2 = new Button("X");
        btnClear2.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear2.setOnAction(e -> comboW2.getEditor().clear());
        HBox row2 = new HBox(5, comboW2, btnClear2);
        HBox.setHgrow(comboW2, Priority.ALWAYS);

        uiContainer.getChildren().addAll(row1, row2);
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
    public String getName() { return "Calculate Distance"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @Override
    public String execute(SpaceVisualizer<T> visualizer) {
        if (strategy == null) return "Error: Distance strategy not set.";
        try {
            T w1 = comboW1.getEditor().getText().isEmpty() ? comboW1.getValue() : (T) comboW1.getEditor().getText();
            T w2 = comboW2.getEditor().getText().isEmpty() ? comboW2.getValue() : (T) comboW2.getEditor().getText();

            DistanceFunction<T> func = new DistanceFunction<>("FULL", w1, w2);
            double dist = space.executeFunction(func, strategy);

            visualizer.clearHighlights();
            visualizer.highlightItems(List.of(w1), "#00FFFF");
            visualizer.highlightItems(List.of(w2), "#FF00FF");

            return "Distance between '" + w1 + "' and '" + w2 + "': " + String.format("%.5f", dist);
        } catch (Exception e) {
            return "Error calculating distance. Check inputs.";
        }
    }

    @Override
    public void undo(SpaceVisualizer<T> visualizer) { visualizer.clearHighlights(); }

    @Override
    public void onNodeClicked(T item) {
        if (comboW1.getEditor().getText().isEmpty()) {
            comboW1.getEditor().setText(item.toString());
        } else if (comboW2.getEditor().getText().isEmpty()) {
            comboW2.getEditor().setText(item.toString());
        }
    }

}