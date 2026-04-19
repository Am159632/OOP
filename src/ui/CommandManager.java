package ui;

import actions.AppAction;
import math.DistanceStrategy;
import visuals.SpaceVisualizer;

import javafx.scene.layout.VBox;
import java.util.List;

public class CommandManager<T> {
    private List<SpaceCommand<T>> availableCommands;
    private SpaceCommand<T> activeCommand;
    private DistanceStrategy currentStrategy;

    public CommandManager(List<SpaceCommand<T>> commands, DistanceStrategy initialStrategy) {
        this.availableCommands = commands;
        this.currentStrategy = initialStrategy;
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

    public AppAction<T> generateActiveAction(SpaceVisualizer<T> visualizer) {
        if (activeCommand == null) return null;
        return activeCommand.generateAction(visualizer);
    }

    public void setStrategy(DistanceStrategy strategy) {
        this.currentStrategy = strategy;
        if (activeCommand != null) activeCommand.setStrategy(strategy);
    }

    public SpaceCommand<T> getActiveCommand() {
        return activeCommand;
    }

    public List<SpaceCommand<T>> getAvailableCommands() {
        return availableCommands;
    }
}

