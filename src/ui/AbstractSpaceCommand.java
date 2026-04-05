package ui;

import core.AbstractAnalyzableSpace;
import math.DistanceStrategy;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractSpaceCommand<T> implements SpaceCommand<T> {
    protected AbstractAnalyzableSpace<T> space;
    protected DistanceStrategy strategy;
    protected VBox uiContainer;
    protected Function<String, T> parser;

    public AbstractSpaceCommand(AbstractAnalyzableSpace<T> space,Function<String, T> parser) {
        this.space = space;
        this.uiContainer = new VBox(10);
        this.parser = parser;
    }

    @Override
    public Node getUI() {
        return uiContainer;
    }

    @Override
    public void setStrategy(DistanceStrategy strategy) {
        this.strategy = strategy;
    }

    protected T getComboValue(ComboBox<T> combo) {
        String text = combo.getEditor().getText();
        if (text != null && !text.isEmpty()) {
            return parser.apply(text);
        }
        return combo.getValue();
    }

    protected void fillFirstEmptyCombo(T item, List<ComboBox<T>> combos) {
        for (ComboBox<T> combo : combos) {
            if (combo.getEditor().getText().isEmpty()) {
                combo.getEditor().setText(item.toString());
                break;
            }
        }
    }
}