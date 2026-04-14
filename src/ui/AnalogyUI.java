package ui;

import actions.AppAction;
import extra.CustomAction;
import core.AbstractAnalyzableSpace;
import extra.Term;
import javafx.scene.control.ComboBox;
import visuals.SpaceVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AnalogyUI<T> extends AbstractSpaceCommand<T> {
    private ComboBox<T> comboW1, comboW2, comboW3;

    public AnalogyUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary, Function<String, T> parser) {
        super(space, parser);
        comboW1 = UIUtils.createSearchableComboBox(vocabulary);
        comboW2 = UIUtils.createSearchableComboBox(vocabulary);
        comboW3 = UIUtils.createSearchableComboBox(vocabulary);

        uiContainer.getChildren().addAll(
                UIUtils.createClearableComboRow(comboW1, "Item 1 (King):"),
                UIUtils.createClearableComboRow(comboW2, "Item 2 (- Man):"),
                UIUtils.createClearableComboRow(comboW3, "Item 3 (+ Woman):")
        );
    }

    @Override
    public String getName() { return "Analogy"; }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {

        T word1 = getComboValue(comboW1);
        T word2 = getComboValue(comboW2);
        T word3 = getComboValue(comboW3);

        if (word1 == null || word2 == null || word3 == null) {
            throw new IllegalArgumentException("Please fill all 3 words for the analogy.");
        }

        List<Term<T>> analogyTerms = new ArrayList<>();
        analogyTerms.add(new Term<>(true, word1));
        analogyTerms.add(new Term<>(false, word2));
        analogyTerms.add(new Term<>(true, word3));

        return new CustomAction<>(space, visualizer, strategy, analogyTerms);
    }

    @Override
    public void onNodeClicked(T item) {
        fillFirstEmptyCombo(item, List.of(comboW1, comboW2, comboW3));
    }

}