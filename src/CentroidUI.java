import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.Arrays;
import java.util.List;

public class CentroidUI<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private VBox uiContainer;
    private TextField txtGroup;

    public CentroidUI(AbstractAnalyzableSpace<T> space) {
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
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String input = txtGroup.getText();
        if (input == null || input.isEmpty()) throw new IllegalArgumentException("Empty Input");
        List<T> group = (List<T>) Arrays.asList(input.split("\\s*,\\s*"));
        return new CentroidAction<>(space, visualizer, strategy, group);
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