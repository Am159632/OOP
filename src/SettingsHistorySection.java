import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SettingsHistorySection<T> implements MenuSection {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;
    private TextArea txtConsole;

    public SettingsHistorySection(AppUIManager<T> uiManager, HistoryManager<T> history, TextArea txtConsole) {
        this.uiManager = uiManager;
        this.history = history;
        this.txtConsole = txtConsole;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblDist = new Label("3. Settings & History");
        lblDist.getStyleClass().add("section-title");

        ComboBox<String> distanceBox = new ComboBox<>();
        distanceBox.getItems().addAll(uiManager.getStrategies().keySet());
        distanceBox.setValue("Euclidean");
        distanceBox.setMaxWidth(Double.MAX_VALUE);
        distanceBox.setOnAction(e -> uiManager.setStrategy(uiManager.getStrategies().get(distanceBox.getValue())));

        HBox historyBox = new HBox(10);
        Button btnUndo = new Button("Undo"); btnUndo.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnUndo, Priority.ALWAYS);
        Button btnRedo = new Button("Redo"); btnRedo.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnRedo, Priority.ALWAYS);
        Button btnClear = new Button("Clear"); btnClear.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnClear, Priority.ALWAYS);

        btnUndo.setOnAction(e -> {
            AppAction<T> action = history.undo();
            if (action != null) {
                uiManager.getMultiVisualizer().clearHighlights();
                AppAction<T> prev = history.peekUndo();
                txtConsole.setText(prev != null ? prev.execute() : "Reverted to clean space.");
            }
        });

        btnRedo.setOnAction(e -> {
            AppAction<T> action = history.redo();
            if (action != null) {
                uiManager.getMultiVisualizer().clearHighlights();
                txtConsole.setText(action.execute());
            }
        });

        btnClear.setOnAction(e -> {
            history.clear();
            uiManager.getMultiVisualizer().clearHighlights();
            txtConsole.setText("History cleared. Space is clean.");
        });

        historyBox.getChildren().addAll(btnUndo, btnRedo, btnClear);
        box.getChildren().addAll(lblDist, distanceBox, historyBox);
        return box;
    }
}