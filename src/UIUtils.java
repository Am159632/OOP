import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
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
            sortedVocabulary.addAll(vocabulary);
            sortedVocabulary.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));

            comboBox.getItems().addAll(sortedVocabulary);
        }

        comboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                comboBox.setItems(FXCollections.observableArrayList(sortedVocabulary));
            } else {
                List<T> filtered = sortedVocabulary.stream()
                        .filter(item -> item.toString().toLowerCase().startsWith(newText.toLowerCase()))
                        .collect(Collectors.toList());
                comboBox.setItems(FXCollections.observableArrayList(filtered));
            }
        });
        return comboBox;
    }
}