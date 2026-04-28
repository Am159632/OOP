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
    private AbstractSpaceVisualizer<T, ?> activeView;
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
        this.multiVisualizer = new MultiSpaceVisualizer<>(new ArrayList<>(activeViews));

        this.strategies = (providedStrategies != null) ? providedStrategies : new ArrayList<>();
        List<SpaceCommand<T>> commands = (providedCommands != null) ? providedCommands : new ArrayList<>();

        DistanceStrategy initialStrategy = this.strategies.isEmpty() ? null : this.strategies.get(0);
        this.commandManager = new CommandManager<>(commands, initialStrategy);

        if (activeViews == null || activeViews.isEmpty()) {
            throw new IllegalArgumentException("At least one GUIVisualizer is required.");
        }

        AbstractSpaceVisualizer<T, ?> initialView = activeViews.getFirst();
        activeView = initialView;
        registerNodeClickHandlers(activeViews);

        centerViewPane = new StackPane(activeView.getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");

        txtConsole = new TextArea("System Ready...\n");
        txtConsole.setEditable(false);
        txtConsole.setWrapText(true);
        txtConsole.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");

        ComboBox<GUIVisualizer<T>> viewSelector = buildViewSelector(activeViews, initialView);
        MenuBar topMenu = buildTopMenu();
        PcaTopBar<T> pcaTopBar = new PcaTopBar<>(this, txtConsole);
        configureViewSelector(viewSelector, pcaTopBar);

        HBox topContainer = new HBox(15);
        topContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topContainer.getStyleClass().add("top-strip");
        topContainer.getChildren().addAll(topMenu, new Label("  View:"), viewSelector, pcaTopBar.build());
        rootPane.setTop(topContainer);

        rootPane.setCenter(centerViewPane);

        SideMenuBuilder<T> builder = new SideMenuBuilder<>(this);
        VBox sideMenu = builder.build(txtConsole, viewSelector);
        rootPane.setRight(sideMenu);

        for (AbstractSpaceVisualizer<T, ?> view : activeViews) {
            activeView = view;
            pcaTopBar.updateDimensions(view.getDimensions());
            pcaTopBar.executePca();
        }
        activeView = initialView;
        centerViewPane.getChildren().setAll(initialView.getVisualNode());
        pcaTopBar.updateDimensions(initialView.getDimensions());
    }

    private void registerNodeClickHandlers(List<AbstractSpaceVisualizer<T, ?>> views) {
        for (AbstractSpaceVisualizer<T, ?> view : views) {
            view.setOnNodeClicked(item -> {
                SpaceCommand<T> activeCmd = commandManager.getActiveCommand();
                if (activeCmd != null) {
                    activeCmd.onNodeClicked(item);
                }
            });
        }
    }


    private MenuBar buildTopMenu() {
        MenuBar topMenu = new MenuBar();
        topMenu.getStyleClass().add("top-strip-menu");
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        Menu viewMenu = new Menu("View");
        MenuItem clearVisualsItem = new MenuItem("Clear Screen");
        clearVisualsItem.setOnAction(e -> activeView.clearSpace());
        viewMenu.getItems().add(clearVisualsItem);

        topMenu.getMenus().addAll(fileMenu, viewMenu);
        return topMenu;
    }

    private ComboBox<GUIVisualizer<T>> buildViewSelector(List<AbstractSpaceVisualizer<T, ?>> activeViews,
                                                         AbstractSpaceVisualizer<T, ?> initialView) {
        ComboBox<GUIVisualizer<T>> viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll(activeViews);
        viewSelector.setValue(initialView);
        return viewSelector;
    }

    private void configureViewSelector(ComboBox<GUIVisualizer<T>> viewSelector, PcaTopBar<T> pcaTopBar) {
        viewSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                activeView = (AbstractSpaceVisualizer<T, ?>) newVal;
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
            activeView.clearHighlights();
            String result = new PcaCommand<>(space, targetAxes).execute(activeView);

            AppAction<T> lastFunc = history.peekUndo();
            if (lastFunc != null) {
                result += "\n[Re-applied]: " + lastFunc.execute();
            }

            return result;
        } catch (Exception e) {
            String details = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Unknown cause"
                    : e.getMessage();
            return "Error: Invalid PCA inputs. " + details;
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
    public AbstractSpaceVisualizer<T, ?> getActiveVisualizer() { return activeView; }
    public MultiSpaceVisualizer<T> getMultiVisualizer() { return multiVisualizer; }
    public List<DistanceStrategy> getStrategies() { return strategies; }
    public List<SpaceCommand<T>> getAvailableCommands() { return commandManager.getAvailableCommands(); }
    public HistoryManager<T> getHistory() { return history; }
    public boolean isZoomEnabled() { return enableZoom;}
    public AbstractAnalyzableSpace<T> getSpace() { return space; }
}
