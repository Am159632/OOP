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

    private T savedStart, savedEnd;
    private int savedK = 0;

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
    public String execute(SpaceVisualizer<T> visualizer) {
        if (strategy == null) return "Error: Distance strategy not set.";
        try {
            String ts = comboStart.getEditor().getText();
            if (ts != null && !ts.isEmpty()) savedStart = (T) ts;
            else if (comboStart.getValue() != null) savedStart = comboStart.getValue();

            String te = comboEnd.getEditor().getText();
            if (te != null && !te.isEmpty()) savedEnd = (T) te;
            else if (comboEnd.getValue() != null) savedEnd = comboEnd.getValue();

            String kText = txtK.getText();
            if (kText != null && !kText.isEmpty()) {
                savedK = Integer.parseInt(kText);
            } else if (savedK == 0) {
                return "Error: Please enter a value for Steps (K).";
            }

            if (savedStart != null) comboStart.getEditor().setText(savedStart.toString());
            if (savedEnd != null) comboEnd.getEditor().setText(savedEnd.toString());
            if (savedK > 0) txtK.setText(String.valueOf(savedK));

            List<ItemDistance<T>> projections = new ArrayList<>();
            for (T item : space.getItems("FULL")) {
                if (item.equals(savedStart) || item.equals(savedEnd)) continue;
                ProjectionFunction<T> func = new ProjectionFunction<>("FULL", item, savedStart, savedEnd);
                double val = space.executeFunction(func, strategy);
                projections.add(new ItemDistance<>(item, val));
            }

            projections.sort(Comparator.comparingDouble(ItemDistance::getDistance));

            List<T> closeToStart = projections.stream().limit(savedK).map(ItemDistance::getId).collect(Collectors.toList());
            List<T> closeToEnd = projections.stream().skip(Math.max(0, projections.size() - savedK)).map(ItemDistance::getId).collect(Collectors.toList());
            java.util.Collections.reverse(closeToEnd);

            visualizer.clearHighlights();
            visualizer.highlightItems(closeToStart, "#FF4500");
            visualizer.highlightItems(closeToEnd, "#32CD32");

            StringBuilder sb = new StringBuilder();
            sb.append("Semantic Line: ").append(savedStart).append(" <--> ").append(savedEnd).append("\n\n");
            sb.append("Top ").append(savedK).append(" closest to '").append(savedStart).append("':\n");
            closeToStart.forEach(word -> sb.append("- ").append(word).append("\n"));
            sb.append("\nTop ").append(savedK).append(" closest to '").append(savedEnd).append("':\n");
            closeToEnd.forEach(word -> sb.append("- ").append(word).append("\n"));

            return sb.toString();
        } catch (Exception e) {
            return "Error executing Semantic Line. Check inputs.";
        }
    }

    @Override
    public void undo(SpaceVisualizer<T> visualizer) {
        visualizer.clearHighlights();
        comboStart.getEditor().clear();
        comboEnd.getEditor().clear();
        comboStart.setValue(null);
        comboEnd.setValue(null);
        txtK.clear();
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