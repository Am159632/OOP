package extra;

import core.AbstractAnalyzableSpace;
import actions.AppAction;
import visuals.SpaceVisualizer;

import javafx.scene.control.TextField;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import ui.AbstractSpaceCommand;
import ui.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MarkerUI<T> extends AbstractSpaceCommand<T> {
    private TextField txtTargets;
    private ColorPicker colorPicker;
    private List<T> vocabulary;

    public MarkerUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary, Function<String, T> parser) {
        super(space, parser);
        this.vocabulary = vocabulary;

        txtTargets = new TextField();

        colorPicker = new ColorPicker(Color.RED);
        colorPicker.setMaxWidth(Double.MAX_VALUE);

        uiContainer.getChildren().addAll(
                UIUtils.createClearableTextRow(txtTargets, "Items to mark (comma separated)"),
                colorPicker
        );
    }

    @Override
    public String getName() {
        return "Highlight Marker";
    }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String input = txtTargets.getText();
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty Targets");
        }

        List<String> rawTargets = Arrays.asList(input.split("\\s*,\\s*"));
        List<T> validTargets = new ArrayList<>();

        for (String s : rawTargets) {
            for (T vocabItem : vocabulary) {
                if (vocabItem.toString().equals(s)) {
                    if (!validTargets.contains(vocabItem)) {
                        validTargets.add(vocabItem);
                    }
                    break;
                }
            }
        }

        if (validTargets.isEmpty()) {
            throw new IllegalArgumentException("No valid targets found");
        }

        Color color = colorPicker.getValue();
        String hex = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));

        return new MarkerAction<>(visualizer, validTargets, hex);
    }

    @Override
    public void onNodeClicked(T item) {
        String current = txtTargets.getText();
        if (current.isEmpty()) {
            txtTargets.setText(item.toString());
        } else {
            txtTargets.setText(current + ", " + item.toString());
        }
    }
}