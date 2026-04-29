package ui;

import actions.AppAction;
import actions.HistoryManager;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;

public class AnalysisSection<T> extends AbstractMenuSection {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;
    private TextArea txtConsole;
    private ComboBox<String> actionBox;

    public AnalysisSection(AppUIManager<T> uiManager, HistoryManager<T> history, TextArea txtConsole) {
        super("Analysis Functions");
        this.uiManager = uiManager;
        this.history = history;
        this.txtConsole = txtConsole;
    }

    @Override
    protected void buildContent(VBox container) {
        List<String> commandNames = new ArrayList<>();
        uiManager.getAvailableCommands().forEach(cmd -> commandNames.add(cmd.getName()));
        commandNames.sort(String::compareTo);

        actionBox = UIUtils.createSearchableComboBox(commandNames);

        if (!uiManager.getAvailableCommands().isEmpty()) {
            actionBox.setValue(commandNames.get(0));
        }
        actionBox.setMaxWidth(Double.MAX_VALUE);

        VBox dynamicInputs = new VBox();
        actionBox.setOnAction(e -> uiManager.updateActiveCommand(actionBox.getValue(), dynamicInputs));
        uiManager.updateActiveCommand(actionBox.getValue(), dynamicInputs);

        Button btnFunc = new Button("Execute Function");
        btnFunc.setMaxWidth(Double.MAX_VALUE);
        btnFunc.setOnAction(e -> {
            try {
                AppAction<T> action = uiManager.generateActiveAction();
                if (action != null) {
                    if (uiManager.getMultiVisualizer() != null) uiManager.getMultiVisualizer().clearHighlights();
                    String res = action.execute();
                    txtConsole.setText(res);
                    if (res != null && !res.startsWith("Error:")) {
                        history.addAction(action);
                    }
                }
            } catch (Exception ex) {
                txtConsole.setText("Error executing function.");
            }
        });

        container.getChildren().addAll(actionBox, dynamicInputs, btnFunc);
    }
}