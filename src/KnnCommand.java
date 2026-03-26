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

    public KnnCommand(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboTarget = createSearchableComboBox(); comboTarget.setPromptText("Target Item");
        Button btnClear = new Button("X"); btnClear.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> comboTarget.getEditor().clear());
        HBox row = new HBox(5, comboTarget, btnClear); HBox.setHgrow(comboTarget, Priority.ALWAYS);

        txtK = new TextField("5"); txtK.setPromptText("Number of Neighbors (K)");

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
            T targetId = comboTarget.getEditor().getText().isEmpty() ? comboTarget.getValue() : (T) comboTarget.getEditor().getText();
            int k = Integer.parseInt(txtK.getText());

            KnnFunction<T> knnFunc = new KnnFunction<>("FULL", targetId, k);
            List<ItemDistance<T>> neighbors = space.executeFunction(knnFunc, strategy);
            List<T> neighborIds = neighbors.stream().map(ItemDistance::getId).collect(Collectors.toList());

            visualizer.clearHighlights();
            visualizer.highlightItems(List.of(targetId), "#FF0000");
            visualizer.highlightItems(neighborIds, "#32CD32");

            StringBuilder sb = new StringBuilder("Neighbors of '" + targetId + "':\n");
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
    public void undo(SpaceVisualizer<T> visualizer) { visualizer.clearHighlights(); }

    @Override
    public void onNodeClicked(T item) {
        if (comboTarget.getEditor().getText().isEmpty()) {
            comboTarget.getEditor().setText(item.toString());
        }
    }

}