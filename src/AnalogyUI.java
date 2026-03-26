import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.stream.Collectors;

public class AnalogyUI<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboW1, comboW2, comboW3;

    public AnalogyUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
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
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String t1 = comboW1.getEditor().getText(); T w1 = (t1 != null && !t1.isEmpty()) ? (T) t1 : comboW1.getValue();
        String t2 = comboW2.getEditor().getText(); T w2 = (t2 != null && !t2.isEmpty()) ? (T) t2 : comboW2.getValue();
        String t3 = comboW3.getEditor().getText(); T w3 = (t3 != null && !t3.isEmpty()) ? (T) t3 : comboW3.getValue();

        if (w1 == null || w2 == null || w3 == null) throw new IllegalArgumentException("Empty Inputs");

        return new AnalogyAction<>(space, visualizer, strategy, w1, w2, w3);
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