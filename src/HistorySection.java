import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HistorySection<T> implements MenuSection {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;
    private TextArea txtConsole;

    public HistorySection(AppUIManager<T> uiManager, HistoryManager<T> history, TextArea txtConsole) {
        this.uiManager = uiManager;
        this.history = history;
        this.txtConsole = txtConsole;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblHistory = new Label("Action History");
        lblHistory.getStyleClass().add("section-title");

        HBox historyBox = new HBox(5); // רווח קטן יותר בין הכפתורים
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
        box.getChildren().addAll(lblHistory, historyBox);
        return box;
    }
}