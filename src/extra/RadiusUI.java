package extra;

import core.*;
import actions.*;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ui.AbstractSpaceCommand;
import ui.UIUtils;
import visuals.SpaceVisualizer;

import java.util.List;
import java.util.function.Function;

public class RadiusUI<T> extends AbstractSpaceCommand<T> {
    private ComboBox<T> comboTarget;
    private TextField txtMinRadius;
    private TextField txtMaxRadius;

    public RadiusUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary, Function<String, T> parser) {
        super(space, parser);
        comboTarget = UIUtils.createSearchableComboBox(vocabulary);

        txtMinRadius = new TextField();
        txtMinRadius.setPromptText("Min Distance");

        txtMaxRadius = new TextField();
        txtMaxRadius.setPromptText("Max Distance");

        uiContainer.getChildren().addAll(
                UIUtils.createClearableComboRow(comboTarget, "Target Item"),
                txtMinRadius,
                txtMaxRadius
        );
    }

    @Override
    public String getName() { return "Radius Search"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        T target = getComboValue(comboTarget);
        if (target == null) throw new IllegalArgumentException("Empty Target");

        double minR = Double.parseDouble(txtMinRadius.getText());
        double maxR = Double.parseDouble(txtMaxRadius.getText());

        return new RadiusAction<>(space, visualizer, strategy, target, minR, maxR);
    }

    @Override
    public void onNodeClicked(T item) {
        fillFirstEmptyCombo(item, List.of(comboTarget));
    }
}