package ui;

import core.*;
import actions.*;
import javafx.scene.control.TextField;
import visuals.SpaceVisualizer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CentroidUI<T> extends AbstractSpaceCommand<T> {
    private TextField txtGroup;

    public CentroidUI(AbstractAnalyzableSpace<T> space, Function<String, T> parser) {
        super(space, parser);
        txtGroup = new TextField();
        uiContainer.getChildren().add(UIUtils.createClearableTextRow(txtGroup, "Items separated by commas"));
    }

    @Override
    public String getName() { return "Centroid (Average)"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String input = txtGroup.getText();
        if (input == null || input.isEmpty()) throw new IllegalArgumentException("Empty Input");

        List<T> group = Arrays.stream(input.split("\\s*,\\s*"))
                .map(parser)
                .collect(Collectors.toList());

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