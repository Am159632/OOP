package ui;

import core.*;
import math.*;
import actions.*;
import visuals.*;

import actions.AppAction;
import visuals.SpaceVisualizer;
import javafx.scene.Node;

public interface SpaceCommand<T> {
    String getName();
    Node getUI();
    void setStrategy(DistanceStrategy strategy);
    void onNodeClicked(T item);
    AppAction<T> generateAction(SpaceVisualizer<T> visualizer);
}