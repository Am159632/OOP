package ui;

import visuals.GUIVisualizer;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;

public class SideMenuBuilder<T> {
    private AppUIManager<T> uiManager;

    public SideMenuBuilder(AppUIManager<T> uiManager) {
        this.uiManager = uiManager;
    }

    public VBox build(TextArea console, ComboBox<GUIVisualizer<T>> selector) {
        VBox sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(350);

        List<MenuSection> sections = new ArrayList<>();

        if (uiManager.isZoomEnabled()) {
            sections.add(new ZoomSection<>(selector));
        }

        if (!uiManager.getStrategies().isEmpty() && !uiManager.getAvailableCommands().isEmpty()) {
            sections.add(new AnalysisSection<>(uiManager, uiManager.getHistory(), console));
            sections.add(new CalculationMethodSection<>(uiManager));
        }

        sections.add(new HistorySection<>(uiManager, uiManager.getHistory(), console));

        for (MenuSection section : sections) {
            sideMenu.getChildren().addAll(section.build(), new Separator());
        }

        VBox.setVgrow(console, Priority.ALWAYS);
        sideMenu.getChildren().add(console);

        return sideMenu;
    }
}