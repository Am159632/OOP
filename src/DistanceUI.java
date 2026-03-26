import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.stream.Collectors;

public class DistanceUI<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboW1, comboW2;

    public DistanceUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboW1 = UIUtils.createSearchableComboBox(vocabulary);
        comboW1.setPromptText("Item 1");
        Button btnClear1 = new Button("X");
        btnClear1.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear1.setOnAction(e -> comboW1.getEditor().clear());
        HBox row1 = new HBox(5, comboW1, btnClear1);
        HBox.setHgrow(comboW1, Priority.ALWAYS);

        comboW2 = UIUtils.createSearchableComboBox(vocabulary);
        comboW2.setPromptText("Item 2");
        Button btnClear2 = new Button("X");
        btnClear2.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear2.setOnAction(e -> comboW2.getEditor().clear());
        HBox row2 = new HBox(5, comboW2, btnClear2);
        HBox.setHgrow(comboW2, Priority.ALWAYS);

        uiContainer.getChildren().addAll(row1, row2);
    }

    @Override
    public String getName() { return "Calculate Distance"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String t1Text = comboW1.getEditor().getText();
        T w1 = (t1Text != null && !t1Text.isEmpty()) ? (T) t1Text : comboW1.getValue();

        String t2Text = comboW2.getEditor().getText();
        T w2 = (t2Text != null && !t2Text.isEmpty()) ? (T) t2Text : comboW2.getValue();

        if (w1 == null || w2 == null) throw new IllegalArgumentException("Empty Inputs");

        return new DistanceAction<>(space, visualizer, strategy, w1, w2);
    }

    @Override
    public void onNodeClicked(T item) {
        if (comboW1.getEditor().getText().isEmpty()) {
            comboW1.getEditor().setText(item.toString());
        } else if (comboW2.getEditor().getText().isEmpty()) {
            comboW2.getEditor().setText(item.toString());
        } else {
            comboW1.getEditor().setText(item.toString());
            comboW2.getEditor().clear();
        }
    }
}