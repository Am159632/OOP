import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.Arrays;
import java.util.List;

public class CentroidCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private VBox uiContainer;
    private TextField txtGroup;

    private String savedInput = "";

    public CentroidCommand(AbstractAnalyzableSpace<T> space) {
        this.space = space;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        txtGroup = new TextField(); txtGroup.setPromptText("Items separated by commas");
        Button btnClear = new Button("Clear"); btnClear.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> txtGroup.clear());
        HBox row = new HBox(5, txtGroup, btnClear); HBox.setHgrow(txtGroup, Priority.ALWAYS);

        uiContainer.getChildren().add(row);
    }

    @Override
    public String getName() { return "Centroid (Average)"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @SuppressWarnings("unchecked")
    @Override
    public String execute(SpaceVisualizer<T> visualizer) {
        if (strategy == null) return "Error: Distance strategy not set.";
        try {
            String input = txtGroup.getText();
            if (input != null && !input.isEmpty()) savedInput = input;

            txtGroup.setText(savedInput);

            List<T> group = (List<T>) Arrays.asList(savedInput.split("\\s*,\\s*"));
            CentroidFunction<T> centroidFunc = new CentroidFunction<>("FULL", group);
            T result = space.executeFunction(centroidFunc, strategy);

            visualizer.clearHighlights();
            if (result != null) {
                visualizer.highlightItems(group, "#ADD8E6");
                visualizer.highlightItems(List.of(result), "#FF0000");
                return "Centroid of the group is: " + result;
            }
            return "No centroid found.";
        } catch (Exception e) {
            return "Error executing Centroid. Check inputs.";
        }
    }

    @Override
    public void undo(SpaceVisualizer<T> visualizer) {
        visualizer.clearHighlights();
        txtGroup.clear();
    }

    @Override
    public void onNodeClicked(T item) {
        String current = txtGroup.getText();
        if (current.isEmpty()) {
            txtGroup.setText(item.toString());
        } else {
            txtGroup.setText(current + ", " + item.toString());
        }
    }
}