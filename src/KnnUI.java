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

public class KnnUI<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private DistanceStrategy strategy;
    private List<T> vocabulary;
    private VBox uiContainer;
    private ComboBox<T> comboTarget;
    private TextField txtK;

    public KnnUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary) {
        this.space = space;
        this.vocabulary = vocabulary;
        buildUI();
    }

    private void buildUI() {
        uiContainer = new VBox(10);

        comboTarget = UIUtils.createSearchableComboBox(vocabulary);
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


    @Override
    public String getName() { return "Find Neighbors (KNN)"; }

    @Override
    public Node getUI() { return uiContainer; }

    @Override
    public void setStrategy(DistanceStrategy strategy) { this.strategy = strategy; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String targetText = comboTarget.getEditor().getText();
        T target = (targetText != null && !targetText.isEmpty()) ? (T) targetText : comboTarget.getValue();
        if (target == null) throw new IllegalArgumentException("Empty Target");

        int k = Integer.parseInt(txtK.getText());
        return new KnnAction<>(space, visualizer, strategy, target, k);
    }

    @Override
    public void onNodeClicked(T item) {
        comboTarget.getEditor().setText(item.toString());
    }
}