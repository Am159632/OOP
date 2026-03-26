import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.stream.Collectors;

public class AnalogyCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboW1, comboW2, comboW3;

    private T savedW1, savedW2, savedW3;

    public AnalogyCommand(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboW1 = createSearchableComboBox(); comboW1.setPromptText("Item 1");
        Button btnClear1 = new Button("X"); btnClear1.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear1.setOnAction(e -> comboW1.getEditor().clear());
        HBox row1 = new HBox(5, comboW1, btnClear1); HBox.setHgrow(comboW1, Priority.ALWAYS);

        comboW2 = createSearchableComboBox(); comboW2.setPromptText("Item 2");
        Button btnClear2 = new Button("X"); btnClear2.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear2.setOnAction(e -> comboW2.getEditor().clear());
        HBox row2 = new HBox(5, comboW2, btnClear2); HBox.setHgrow(comboW2, Priority.ALWAYS);

        comboW3 = createSearchableComboBox(); comboW3.setPromptText("Item 3");
        Button btnClear3 = new Button("X"); btnClear3.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear3.setOnAction(e -> comboW3.getEditor().clear());
        HBox row3 = new HBox(5, comboW3, btnClear3); HBox.setHgrow(comboW3, Priority.ALWAYS);

        uiContainer.getChildren().addAll(row1, row2, row3);
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
    public String getName() { return "Analogy"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @Override
    public String execute(SpaceVisualizer<T> visualizer) {
        if (strategy == null) return "Error: Distance strategy not set.";
        try {
            String t1 = comboW1.getEditor().getText();
            if (t1 != null && !t1.isEmpty()) savedW1 = (T) t1;
            else if (comboW1.getValue() != null) savedW1 = comboW1.getValue();

            String t2 = comboW2.getEditor().getText();
            if (t2 != null && !t2.isEmpty()) savedW2 = (T) t2;
            else if (comboW2.getValue() != null) savedW2 = comboW2.getValue();

            String t3 = comboW3.getEditor().getText();
            if (t3 != null && !t3.isEmpty()) savedW3 = (T) t3;
            else if (comboW3.getValue() != null) savedW3 = comboW3.getValue();

            if (savedW1 != null) comboW1.getEditor().setText(savedW1.toString());
            if (savedW2 != null) comboW2.getEditor().setText(savedW2.toString());
            if (savedW3 != null) comboW3.getEditor().setText(savedW3.toString());

            AnalogyFunction<T> analogyFunc = new AnalogyFunction<>("FULL", savedW1, savedW2, savedW3);
            T result = space.executeFunction(analogyFunc, strategy);

            visualizer.clearHighlights();
            if (result != null) {
                visualizer.highlightItems(List.of(savedW1, savedW2, savedW3), "#FFA500");
                visualizer.highlightItems(List.of(result), "#FFD700");
                return savedW1 + " - " + savedW2 + " + " + savedW3 + " = " + result;
            }
            return "No analogy found.";
        } catch (Exception e) {
            return "Error executing Analogy. Check inputs.";
        }
    }

    @Override
    public void undo(SpaceVisualizer<T> visualizer) {
        visualizer.clearHighlights();
        comboW1.getEditor().clear();
        comboW2.getEditor().clear();
        comboW3.getEditor().clear();
        comboW1.setValue(null);
        comboW2.setValue(null);
        comboW3.setValue(null);
    }

    @Override
    public void onNodeClicked(T item) {
        if (comboW1.getEditor().getText().isEmpty()) {
            comboW1.getEditor().setText(item.toString());
        } else if (comboW2.getEditor().getText().isEmpty()) {
            comboW2.getEditor().setText(item.toString());
        } else if (comboW3.getEditor().getText().isEmpty()) {
            comboW3.getEditor().setText(item.toString());
        } else {
            comboW1.getEditor().setText(item.toString());
            comboW2.getEditor().clear();
            comboW3.getEditor().clear();
        }
    }
}