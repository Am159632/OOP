package ui;

import actions.HistoryManager;
import visuals.GUIVisualizer;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class SideMenuBuilder<T> {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;

    public SideMenuBuilder(AppUIManager<T> uiManager, HistoryManager<T> history) {
        this.uiManager = uiManager;
        this.history = history;
    }

    public VBox build(TextArea console, ComboBox<GUIVisualizer<T>> selector) {
        VBox sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(350);

        List<MenuSection> sections = List.of(
                new PcaSection<>(uiManager, selector, console),
                new AnalysisSection<>(uiManager, history, console),
                new CalculationMethodSection<>(uiManager),
                new HistorySection<>(uiManager, history, console),
                new ZoomSection<>(selector)
        );

        sideMenu.getChildren().addAll(selector, new Separator());

        for (MenuSection section : sections) {
            sideMenu.getChildren().addAll(section.build(), new Separator());
        }

        VBox.setVgrow(console, Priority.ALWAYS);
        sideMenu.getChildren().add(console);

        return sideMenu;
    }
}