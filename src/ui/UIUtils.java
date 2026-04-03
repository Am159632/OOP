package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UIUtils {

    public static <T> ComboBox<T> createSearchableComboBox(List<T> vocabulary) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setEditable(true);
        comboBox.setMaxWidth(Double.MAX_VALUE);

        List<T> sortedVocabulary = new ArrayList<>();
        if (vocabulary != null) {
            for (T item : vocabulary) {
                if (item != null && !item.toString().trim().isEmpty() && !sortedVocabulary.contains(item)) {
                    sortedVocabulary.add(item);
                }
            }
            sortedVocabulary.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));
        }

        ObservableList<T> originalItems = FXCollections.observableArrayList(sortedVocabulary);
        comboBox.setItems(originalItems);

        comboBox.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
                    event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT ||
                    event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END ||
                    event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
                return;
            }

            TextField editor = comboBox.getEditor();
            String text = editor.getText();
            int caretPos = editor.getCaretPosition();

            if (text == null || text.isEmpty()) {
                comboBox.setItems(originalItems);
                comboBox.hide();
            } else {
                String search = text.toLowerCase();

                List<T> startW = sortedVocabulary.stream()
                        .filter(item -> item.toString().toLowerCase().startsWith(search))
                        .collect(Collectors.toList());

                List<T> cont = sortedVocabulary.stream()
                        .filter(item -> item.toString().toLowerCase().contains(search) &&
                                !item.toString().toLowerCase().startsWith(search))
                        .collect(Collectors.toList());

                List<T> filtered = new ArrayList<>(startW);
                filtered.addAll(cont);

                comboBox.setItems(FXCollections.observableArrayList(filtered));

                if (!filtered.isEmpty()) {
                    comboBox.show();
                } else {
                    comboBox.hide();
                }
            }
            editor.setText(text);
            editor.positionCaret(caretPos);
        });

        return comboBox;
    }

    public static <T> javafx.scene.layout.HBox createClearableComboRow(ComboBox<T> combo, String prompt) {
        combo.setPromptText(prompt);
        javafx.scene.control.Button btnClear = new javafx.scene.control.Button("X");
        btnClear.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> combo.getEditor().clear());
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(5, combo, btnClear);
        javafx.scene.layout.HBox.setHgrow(combo, javafx.scene.layout.Priority.ALWAYS);
        return row;
    }

    public static javafx.scene.layout.HBox createClearableTextRow(TextField txt, String prompt) {
        txt.setPromptText(prompt);
        javafx.scene.control.Button btnClear = new javafx.scene.control.Button("X");
        btnClear.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> txt.clear());
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(5, txt, btnClear);
        javafx.scene.layout.HBox.setHgrow(txt, javafx.scene.layout.Priority.ALWAYS);
        return row;
    }
}