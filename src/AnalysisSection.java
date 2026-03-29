import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AnalysisSection<T> implements MenuSection {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;
    private TextArea txtConsole;
    private ComboBox<String> actionBox;

    public AnalysisSection(AppUIManager<T> uiManager, HistoryManager<T> history, TextArea txtConsole) {
        this.uiManager = uiManager;
        this.history = history;
        this.txtConsole = txtConsole;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblFunc = new Label("Analysis Functions");
        lblFunc.getStyleClass().add("section-title");

        actionBox = new ComboBox<>();
        uiManager.getAvailableCommands().forEach(cmd -> actionBox.getItems().add(cmd.getName()));
        if (!uiManager.getAvailableCommands().isEmpty()) {
            actionBox.setValue(uiManager.getAvailableCommands().get(0).getName());
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
                    uiManager.getMultiVisualizer().clearHighlights();
                    String res = action.execute();
                    txtConsole.setText(res);
                    history.addAction(action);
                }
            } catch (Exception ex) {
                txtConsole.setText("Error executing function.");
            }
        });

        box.getChildren().addAll(lblFunc, actionBox, dynamicInputs, btnFunc);
        return box;
    }
}