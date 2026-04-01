package ui;

import core.*;
import math.*;
import actions.*;
import visuals.*;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.util.*;

public class AppUIManager<T> {
    private BorderPane rootPane;
    private AbstractAnalyzableSpace<T> space;
    private MultiSpaceVisualizer<T> multiVisualizer;
    private StackPane centerViewPane;

    private Map<String, DistanceStrategy> strategies;
    private DistanceStrategy currentStrategy;
    private List<SpaceCommand<T>> availableCommands;
    private SpaceCommand<T> activeCommand;
    private boolean enableZoom;

    private HistoryManager<T> history;
    private Map<Integer, String[]> pcaHistory = new HashMap<>();

    public AppUIManager(AbstractAnalyzableSpace<T> space,
                        Map<String, DistanceStrategy> providedStrategies,
                        List<AbstractSpaceVisualizer<T, ?>> activeViews,
                        List<SpaceCommand<T>> providedCommands,boolean enableZoom) {

        this.space = space;
        this.rootPane = new BorderPane();
        this.history = new HistoryManager<>();
        this.enableZoom = enableZoom;

        this.strategies = (providedStrategies != null) ? providedStrategies : new HashMap<>();
        this.availableCommands = (providedCommands != null) ? providedCommands : new ArrayList<>();

        if (!this.strategies.isEmpty()) {
            this.currentStrategy = this.strategies.values().iterator().next();
        }

        multiVisualizer = new MultiSpaceVisualizer<>(new ArrayList<>(activeViews));

        centerViewPane = new StackPane(activeViews.get(0).getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");

        multiVisualizer.setOnNodeClicked(item -> {
            if (activeCommand != null) activeCommand.onNodeClicked(item);
        });

        SideMenuBuilder<T> builder = new SideMenuBuilder<>(this);
        TextArea txtConsole = new TextArea("System Ready...\n");
        txtConsole.setEditable(false);
        txtConsole.setWrapText(true);
        txtConsole.setPrefRowCount(8);

        ComboBox<GUIVisualizer<T>> viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll(activeViews);
        viewSelector.setValue(activeViews.get(0));

        VBox sideMenu = builder.build(txtConsole, viewSelector);
        rootPane.setRight(sideMenu);
        rootPane.setCenter(centerViewPane);

        int initialDimensions = activeViews.get(0).getDimensions();
        updatePcaLogic(getSavedPcaValues(initialDimensions));
    }

    public BorderPane getRoot() { return rootPane; }
    public StackPane getCenterViewPane() { return centerViewPane; }
    public MultiSpaceVisualizer<T> getMultiVisualizer() { return multiVisualizer; }
    public Map<String, DistanceStrategy> getStrategies() { return strategies; }
    public List<SpaceCommand<T>> getAvailableCommands() { return availableCommands; }
    public HistoryManager<T> getHistory() { return history; }
    public boolean isZoomEnabled() { return enableZoom;}

    public String[] getSavedPcaValues(int dimensions) {
        return pcaHistory.computeIfAbsent(dimensions, dim -> {
            String[] defaultValues = new String[dim];
            for (int i = 0; i < dim; i++) {
                defaultValues[i] = String.valueOf(i);
            }
            return defaultValues;
        });
    }

    public String updatePcaLogic(String[] axes) {
        pcaHistory.put(axes.length, axes);

        int[] targetAxes = new int[axes.length];
        for (int i = 0; i < axes.length; i++) {
            targetAxes[i] = Integer.parseInt(axes[i]);
        }

        try {
            multiVisualizer.clearHighlights();

            String result = new PcaCommand<>(space, targetAxes).execute(multiVisualizer);

            AppAction<T> lastFunc = history.peekUndo();
            if (lastFunc != null) {
                result += "\n[Re-applied]: " + lastFunc.execute();
            }
            return result;
        } catch (Exception e) {
            return "Error: Invalid PCA inputs.";
        }
    }

    public void updateActiveCommand(String name, VBox container) {
        container.getChildren().clear();
        for (SpaceCommand<T> cmd : availableCommands) {
            if (cmd.getName().equals(name)) {
                activeCommand = cmd;
                activeCommand.setStrategy(currentStrategy);
                container.getChildren().add(activeCommand.getUI());
                break;
            }
        }
    }

    public AppAction<T> generateActiveAction() {
        if (activeCommand == null) return null;
        return activeCommand.generateAction(multiVisualizer);
    }

    public void setStrategy(DistanceStrategy s) {
        this.currentStrategy = s;
        if (activeCommand != null) activeCommand.setStrategy(s);
    }
}