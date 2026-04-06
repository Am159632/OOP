package extra;

import actions.AppAction;
import core.AbstractAnalyzableSpace;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ui.AbstractSpaceCommand;
import visuals.SpaceVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CustomUI<T> extends AbstractSpaceCommand<T> {
    private TextField txtEquation;

    public CustomUI(AbstractAnalyzableSpace<T> space, List<T> vocabulary, Function<String, T> parser) {
        super(space, parser);

        txtEquation = new TextField();
        txtEquation.setPromptText("e.g. King - Man + Woman");
        HBox.setHgrow(txtEquation, Priority.ALWAYS);

        Button btnAdd = new Button("+");
        Button btnSub = new Button("-");
        Button btnBackspace = new Button("Del");
        Button btnClear = new Button("Clear");

        btnAdd.setOnAction(e -> appendText(" + "));
        btnSub.setOnAction(e -> appendText(" - "));

        btnBackspace.setOnAction(e -> {
            String text = txtEquation.getText().trim();
            if (!text.isEmpty()) {
                int lastSpace = text.lastIndexOf(' ');
                if (lastSpace > 0) {
                    txtEquation.setText(text.substring(0, lastSpace).trim());
                } else {
                    txtEquation.clear();
                }
            }
        });

        btnClear.setOnAction(e -> txtEquation.clear());

        HBox controls = new HBox(5, btnAdd, btnSub, btnBackspace, btnClear);
        uiContainer.getChildren().addAll(txtEquation, controls);
    }

    private void appendText(String str) {
        String current = txtEquation.getText();
        if (!current.isEmpty() && !current.endsWith(" ")) {
            txtEquation.setText(current + str);
        } else {
            txtEquation.setText(current + str.trim() + " ");
        }
        txtEquation.positionCaret(txtEquation.getText().length());
    }

    @Override
    public String getName() {
        return "Custom Equation";
    }

    @Override
    public AppAction<T> generateAction(SpaceVisualizer<T> visualizer) {
        String input = txtEquation.getText().trim();
        if (input.isEmpty()) throw new IllegalArgumentException("Empty Equation");

        input = input.replace("+", " + ").replace("-", " - ").replaceAll("\\s+", " ").trim();
        String[] tokens = input.split(" ");

        List<Term<T>> terms = new ArrayList<>();
        boolean isNextAdd = true;

        for (String token : tokens) {
            if (token.equals("+")) {
                isNextAdd = true;
            } else if (token.equals("-")) {
                isNextAdd = false;
            } else {
                T item = parser.apply(token);
                terms.add(new Term<>(isNextAdd, item));
                isNextAdd = true;
            }
        }

        if (terms.isEmpty()) throw new IllegalArgumentException("No valid terms found");

        return new CustomAction<>(space, visualizer, strategy, terms);
    }

    @Override
    public void onNodeClicked(T item) {
        String current = txtEquation.getText().trim();

        if (current.isEmpty() || current.endsWith("+") || current.endsWith("-")) {
            txtEquation.setText(current + (current.isEmpty() ? "" : " ") + item.toString());
        } else {
            txtEquation.setText(current + " + " + item.toString());
        }
    }
}