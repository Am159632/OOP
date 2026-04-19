package ui;

import core.*;
import math.*;
import actions.*;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import visuals.AbstractSpaceVisualizer;
import visuals.GUIVisualizer;
import visuals.MultiSpaceVisualizer;

import java.util.*;

public class AppUIManager<T> {
    private BorderPane rootPane;
    private AbstractAnalyzableSpace<T> space;
    private MultiSpaceVisualizer<T> multiVisualizer;
    private StackPane centerViewPane;
    private TextArea txtConsole;

    private List<DistanceStrategy> strategies;
    private CommandManager<T> commandManager;
    private boolean enableZoom;

    private HistoryManager<T> history;
    private Map<Integer, String[]> pcaHistory = new HashMap<>();

    public AppUIManager(AbstractAnalyzableSpace<T> space,
                        List<DistanceStrategy> providedStrategies,
                        List<AbstractSpaceVisualizer<T, ?>> activeViews,
                        List<SpaceCommand<T>> providedCommands, boolean enableZoom) {

        this.space = space;
        this.rootPane = new BorderPane();
        this.history = new HistoryManager<>();
        this.enableZoom = enableZoom;

        this.strategies = (providedStrategies != null) ? providedStrategies : new ArrayList<>();
        List<SpaceCommand<T>> commands = (providedCommands != null) ? providedCommands : new ArrayList<>();

        DistanceStrategy initialStrategy = this.strategies.isEmpty() ? null : this.strategies.get(0);
        this.commandManager = new CommandManager<>(commands, initialStrategy);

        multiVisualizer = new MultiSpaceVisualizer<>(new ArrayList<>(activeViews));
        centerViewPane = new StackPane(activeViews.get(0).getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");
        multiVisualizer.setOnNodeClicked(item -> {
            SpaceCommand<T> activeCmd = commandManager.getActiveCommand();
            if (activeCmd != null) activeCmd.onNodeClicked(item);
        });

        txtConsole = new TextArea("System Ready...\n");
        txtConsole.setEditable(false);
        txtConsole.setWrapText(true);
        txtConsole.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");

        ComboBox<GUIVisualizer<T>> viewSelector = buildViewSelector(activeViews);
        MenuBar topMenu = buildTopMenu();
        PcaTopBar<T> pcaTopBar = new PcaTopBar<>(this, txtConsole);
        configureViewSelector(viewSelector, pcaTopBar);

        HBox topContainer = new HBox(15);
        topContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topContainer.getChildren().addAll(topMenu, new Label("  View:"), viewSelector, pcaTopBar.build());
        rootPane.setTop(topContainer);

        rootPane.setCenter(centerViewPane);

        SideMenuBuilder<T> builder = new SideMenuBuilder<>(this);
        VBox sideMenu = builder.build(txtConsole, viewSelector);
        rootPane.setRight(sideMenu);

        pcaTopBar.updateDimensions(activeViews.get(0).getDimensions());
    }

    private MenuBar buildTopMenu() {
        MenuBar topMenu = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        Menu viewMenu = new Menu("View");
        MenuItem clearVisualsItem = new MenuItem("Clear Screen");
        clearVisualsItem.setOnAction(e -> multiVisualizer.clearHighlights());
        viewMenu.getItems().add(clearVisualsItem);

        topMenu.getMenus().addAll(fileMenu, viewMenu);
        return topMenu;
    }

    private ComboBox<GUIVisualizer<T>> buildViewSelector(List<AbstractSpaceVisualizer<T, ?>> activeViews) {
        ComboBox<GUIVisualizer<T>> viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll(activeViews);
        viewSelector.setValue(activeViews.get(0));
        return viewSelector;
    }

    private void configureViewSelector(ComboBox<GUIVisualizer<T>> viewSelector, PcaTopBar<T> pcaTopBar) {
        viewSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                centerViewPane.getChildren().setAll(newVal.getVisualNode());
                pcaTopBar.updateDimensions(newVal.getDimensions());
            }
        });
    }

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
        commandManager.updateActiveCommand(name, container);
    }

    public AppAction<T> generateActiveAction() {
        return commandManager.generateActiveAction(multiVisualizer);
    }

    public void setStrategy(DistanceStrategy s) {
        commandManager.setStrategy(s);
    }

    public BorderPane getRoot() { return rootPane; }
    public StackPane getCenterViewPane() { return centerViewPane; }
    public MultiSpaceVisualizer<T> getMultiVisualizer() { return multiVisualizer; }
    public List<DistanceStrategy> getStrategies() { return strategies; }
    public List<SpaceCommand<T>> getAvailableCommands() { return commandManager.getAvailableCommands(); }
    public HistoryManager<T> getHistory() { return history; }
    public boolean isZoomEnabled() { return enableZoom;}
    public AbstractAnalyzableSpace<T> getSpace() { return space; }
}