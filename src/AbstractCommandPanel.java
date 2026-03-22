import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractCommandPanel<T> implements ViewModeObserver<T> {
    protected VBox panelRoot;
    protected AbstractAnalyzableSpace<T> space;
    protected SpaceVisualizer<T> currentVisualizer;
    protected DistanceStrategy strategy;
    protected Consumer<String> consoleLogger;
    protected List<T> vocabulary;

    public AbstractCommandPanel(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, Consumer<String> logger, List<T> vocabulary) {
        this.space = space;
        this.currentVisualizer = visualizer;
        this.strategy = strategy;
        this.consoleLogger = logger;
        this.vocabulary = vocabulary;
        this.panelRoot = new VBox(10);
    }

    public VBox getPanel() {
        return panelRoot;
    }

    public void buildPanel() {
        panelRoot.getChildren().clear();
        buildInputs();
        Button btnExecute = new Button("בצע: " + getCommandName());
        btnExecute.setMaxWidth(Double.MAX_VALUE);
        btnExecute.setOnAction(e -> executeCommand());
        panelRoot.getChildren().add(btnExecute);
    }

    protected ComboBox<T> createSearchableComboBox() {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setEditable(true);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        if (vocabulary != null) {
            comboBox.getItems().addAll(vocabulary);
        }
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

    public abstract String getCommandName();
    protected abstract void buildInputs();
    public abstract void executeCommand();
    public abstract void onNodeClicked(T item);

    @Override
    public void onViewModeChanged(boolean is3D, SpaceVisualizer<T> activeVisualizer) {
        this.currentVisualizer = activeVisualizer;
    }
}