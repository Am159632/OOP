package ui;

import core.*;
import actions.*;

import javafx.scene.control.TextField;
import visuals.SpaceVisualizer;

import java.util.Arrays;
import java.util.List;

public class CentroidUI<T> extends AbstractSpaceCommand<T> {
    private TextField txtGroup;

    public CentroidUI(AbstractAnalyzableSpace<T> space) {
        super(space);
        txtGroup = new TextField();
        uiContainer.getChildren().add(UIUtils.createClearableTextRow(txtGroup, "Items separated by commas"));
    }

    @Override
    public String getName() { return "Centroid (Average)"; }

    @SuppressWarnings("unchecked")
    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String input = txtGroup.getText();
        if (input == null || input.isEmpty()) throw new IllegalArgumentException("Empty Input");
        List<T> group = (List<T>) Arrays.asList(input.split("\\s*,\\s*"));
        return new CentroidAction<>(space, visualizer, strategy, group);
    }

    @Override
    public void onNodeClicked(T item) {
        String current = txtGroup.getText();
        if (current.isEmpty()) {
            txtGroup.setText(item.toString());
        } else {
            txtGroup.setText(current + ", " + item.toString());
        }
    }
}