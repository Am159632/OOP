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

    private HistoryManager<T> history;
    private String pX2D = "0", pY2D = "1";
    private String pX3D = "0", pY3D = "1", pZ3D = "2";

    public AppUIManager(AbstractAnalyzableSpace<T> space, DistanceStrategy defaultStrategy, List<T> vocabulary) {
        this.space = space;
        this.currentStrategy = defaultStrategy;
        this.rootPane = new BorderPane();
        this.history = new HistoryManager<>();

        List<AbstractSpaceVisualizer<T, ?>> activeViews = List.of(
                new Space2DVisualizer<>(),
                new Space3DVisualizer<>()
        );

        multiVisualizer = new MultiSpaceVisualizer<>(new ArrayList<>(activeViews));

        centerViewPane = new StackPane(activeViews.get(0).getVisualNode());
        centerViewPane.setStyle("-fx-background-color: transparent;");

        strategies = new HashMap<>();
        strategies.put("Euclidean", new EuclideanStrategy());
        strategies.put("Cosine", new CosineStrategy());

        availableCommands = new ArrayList<>();
        availableCommands.add(new KnnUI<>(space, vocabulary));
        availableCommands.add(new DistanceUI<>(space, vocabulary));
        availableCommands.add(new AnalogyUI<>(space, vocabulary));
        availableCommands.add(new CentroidUI<>(space));
        availableCommands.add(new SemanticLineUI<>(space, vocabulary));

        multiVisualizer.setOnNodeClicked(item -> {
            if (activeCommand != null) activeCommand.onNodeClicked(item);
        });

        SideMenuBuilder<T> builder = new SideMenuBuilder<>(this);
        TextArea txtConsole = new TextArea("System Ready...\n");
        txtConsole.setEditable(false); txtConsole.setWrapText(true); txtConsole.setPrefRowCount(8);

        ComboBox<GUIVisualizer<T>> viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll(activeViews);
        viewSelector.setValue(activeViews.get(0));

        VBox sideMenu = builder.build(txtConsole, viewSelector);
        rootPane.setRight(sideMenu);
        rootPane.setCenter(centerViewPane);

        updatePcaLogic("0", "1", "2", false);
    }

    public BorderPane getRoot() { return rootPane; }
    public StackPane getCenterViewPane() { return centerViewPane; }
    public MultiSpaceVisualizer<T> getMultiVisualizer() { return multiVisualizer; }
    public Map<String, DistanceStrategy> getStrategies() { return strategies; }
    public List<SpaceCommand<T>> getAvailableCommands() { return availableCommands; }
    public HistoryManager<T> getHistory() { return history; }

    public String[] getSavedPcaValues(boolean is3D) {
        if (is3D) return new String[]{pX3D, pY3D, pZ3D};
        return new String[]{pX2D, pY2D, pZ3D};
    }

    public String updatePcaLogic(String x, String y, String z, boolean is3D) {
        if (is3D) {
            pX3D = x; pY3D = y; pZ3D = z;
        } else {
            pX2D = x; pY2D = y;
        }

        String realZ = is3D ? z : String.valueOf(Integer.MIN_VALUE);

        try {
            int px = Integer.parseInt(x);
            int py = Integer.parseInt(y);
            int pz = Integer.parseInt(realZ);

            multiVisualizer.clearHighlights();
            String result = new PcaCommand<>(space, px, py, pz).execute(multiVisualizer);

            AppAction<T> lastFunc = history.peekUndo();
            if (lastFunc != null) {
                String funcRes = lastFunc.execute();
                result += "\n[Re-applied]: " + funcRes;
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