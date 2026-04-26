package ui;

import actions.AppAction;
import actions.HistoryManager;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HistorySection<T> extends AbstractMenuSection {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;
    private TextArea txtConsole;

    public HistorySection(AppUIManager<T> uiManager, HistoryManager<T> history, TextArea txtConsole) {
        super("Action History");
        this.uiManager = uiManager;
        this.history = history;
        this.txtConsole = txtConsole;
    }

    @Override
    protected void buildContent(VBox container) {
        HBox historyBox = new HBox(5);
        Button btnUndo = new Button("Undo");
        btnUndo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnUndo, Priority.ALWAYS);
        Button btnRedo = new Button("Redo");
        btnRedo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRedo, Priority.ALWAYS);
        Button btnClear = new Button("Clear");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnClear, Priority.ALWAYS);

        btnUndo.setOnAction(e -> {
            AppAction<T> action = history.undo();
            if (action != null) {
                uiManager.getActiveVisualizer().clearHighlights();
                AppAction<T> prev = history.peekUndo();
                txtConsole.setText(prev != null ? prev.execute() : "Reverted to clean space.");
            }
        });

        btnRedo.setOnAction(e -> {
            AppAction<T> action = history.redo();
            if (action != null) {
                uiManager.getActiveVisualizer().clearHighlights();
                txtConsole.setText(action.execute());
            }
        });

        btnClear.setOnAction(e -> {
            history.clear();
            uiManager.getActiveVisualizer().clearHighlights();
            txtConsole.setText("History cleared. Space is clean.");
        });

        historyBox.getChildren().addAll(btnUndo, btnRedo, btnClear);
        container.getChildren().add(historyBox);
    }
}