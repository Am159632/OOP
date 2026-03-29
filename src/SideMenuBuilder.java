import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SideMenuBuilder<T> {
    private AppUIManager<T> uiManager;
    private HistoryManager<T> history;

    public SideMenuBuilder(AppUIManager<T> uiManager, HistoryManager<T> history) {
        this.uiManager = uiManager;
        this.history = history;
    }

    public VBox build(TextArea console, ComboBox<SpaceVisualizer<T>> selector) {
        VBox sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setPrefWidth(350);

        MenuSection pcaSection = new PcaSection<>(uiManager, selector, console);
        MenuSection analysisSection = new AnalysisSection<>(uiManager, history, console);
        MenuSection calcMethodSection = new CalculationMethodSection<>(uiManager);
        MenuSection settingsHistorySection = new HistorySection<>(uiManager, history, console);
        MenuSection zoomSection = new ZoomSection<>(selector);

        VBox.setVgrow(console, Priority.ALWAYS);
        sideMenu.getChildren().addAll(
                selector, new Separator(),
                pcaSection.build(), new Separator(),
                analysisSection.build(), new Separator(),
                calcMethodSection.build(), new Separator(),
                settingsHistorySection.build(), new Separator(),
                zoomSection.build(), console
        );

        return sideMenu;
    }
}