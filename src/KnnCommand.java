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

public class KnnCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboTarget;
    private TextField txtK;

    private T savedTarget;
    private int savedK = 0; // 0 אומר שעדיין לא הוקלד כלום

    public KnnCommand(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboTarget = createSearchableComboBox();
        comboTarget.setPromptText("Target Item");
        Button btnClear = new Button("X");
        btnClear.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> comboTarget.getEditor().clear());
        HBox row = new HBox(5, comboTarget, btnClear);
        HBox.setHgrow(comboTarget, Priority.ALWAYS);

        txtK = new TextField();
        txtK.setPromptText("Number of Neighbors (K)");

        uiContainer.getChildren().addAll(row, txtK);
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
    public String getName() { return "Find Neighbors (KNN)"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @Override
    public String execute(SpaceVisualizer<T> visualizer) {
        if (strategy == null) return "Error: Distance strategy not set.";
        try {
            String targetText = comboTarget.getEditor().getText();
            if (targetText != null && !targetText.isEmpty()) {
                savedTarget = (T) targetText;
            } else if (comboTarget.getValue() != null) {
                savedTarget = comboTarget.getValue();
            }

            String kText = txtK.getText();
            if (kText != null && !kText.isEmpty()) {
                savedK = Integer.parseInt(kText);
            } else if (savedK == 0) {
                return "Error: Please enter a value for K.";
            }

            // החזרת הערכים לתיבות (למקרה של Redo מתיבות ריקות)
            if (savedTarget != null) comboTarget.getEditor().setText(savedTarget.toString());
            if (savedK > 0) txtK.setText(String.valueOf(savedK));

            KnnFunction<T> knnFunc = new KnnFunction<>("FULL", savedTarget, savedK);
            List<ItemDistance<T>> neighbors = space.executeFunction(knnFunc, strategy);
            List<T> neighborIds = neighbors.stream().map(ItemDistance::getId).collect(Collectors.toList());

            visualizer.clearHighlights();
            visualizer.highlightItems(List.of(savedTarget), "#FF0000");
            visualizer.highlightItems(neighborIds, "#32CD32");

            StringBuilder sb = new StringBuilder("Neighbors of '" + savedTarget + "':\n");
            for (int i = 0; i < neighbors.size(); i++) {
                sb.append(i + 1).append(". ").append(neighbors.get(i).getId())
                        .append(" (Distance: ").append(String.format("%.4f", neighbors.get(i).getDistance())).append(")\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error executing KNN. Check inputs.";
        }
    }

    @Override
    public void undo(SpaceVisualizer<T> visualizer) {
        visualizer.clearHighlights();
        comboTarget.getEditor().clear();
        comboTarget.setValue(null);
        txtK.clear(); // מנקה לחלוטין בלי מספרים כפויים!
    }

    @Override
    public void onNodeClicked(T item) {
        comboTarget.getEditor().setText(item.toString());
    }
}