import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            OurSpace space = new OurSpace();
            DistanceStrategy strategy = new EuclideanStrategy();
            space.loadFiles("C:/Users/asafm/IdeaProjects/OOP/full_vectors.json", "C:/Users/asafm/IdeaProjects/OOP/pca_vectors.json");

            List<String> vocabulary = new ArrayList<>();
            Set<String> items = space.getItems("FULL");
            if (items != null) {
                vocabulary.addAll(items);
            }

            AppUIManager<String> uiManager = new AppUIManager<>(space, strategy, vocabulary);
            Scene scene = new Scene(uiManager.getRoot(), 1200, 750);

            try {
                java.net.URL cssUrl = getClass().getResource("/style.css");
                if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            } catch (Exception ignored) {}

            primaryStage.setScene(scene);
            primaryStage.setTitle("Word Embedding Visualizer");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}