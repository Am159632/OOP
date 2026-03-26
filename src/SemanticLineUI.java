import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticLineUI<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboStart, comboEnd;
    private TextField txtK;

    public SemanticLineUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
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

        txtK = new TextField(); txtK.setPromptText("Amount of Steps (K)");

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
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String ts = comboStart.getEditor().getText(); T start = (ts != null && !ts.isEmpty()) ? (T) ts : comboStart.getValue();
        String te = comboEnd.getEditor().getText(); T end = (te != null && !te.isEmpty()) ? (T) te : comboEnd.getValue();

        if (start == null || end == null) throw new IllegalArgumentException("Empty Inputs");
        int k = Integer.parseInt(txtK.getText());

        return new SemanticLineAction<>(space, visualizer, strategy, start, end, k);
    }

    @Override
    public void onNodeClicked(T item) {
        if (comboStart.getEditor().getText().isEmpty()) {
            comboStart.getEditor().setText(item.toString());
        } else if (comboEnd.getEditor().getText().isEmpty()) {
            comboEnd.getEditor().setText(item.toString());
        } else {
            comboStart.getEditor().setText(item.toString());
            comboEnd.getEditor().clear();
        }
    }
}