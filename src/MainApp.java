import core.*;
import extra.*;
import math.*;
import ui.*;
import visuals.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Function;

public class MainApp extends Application {

    public static void main(String[] args) {
        System.out.println("Starting GUI... Please wait.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            OurSpace space = new OurSpace();
            space.loadFiles("C:/Users/asafm/IdeaProjects/OOP/full_vectors.json", "C:/Users/asafm/IdeaProjects/OOP/pca_vectors.json");

            List<String> vocabulary = new ArrayList<>();
            Set<String> items = space.getItems("FULL");
            if (items != null) vocabulary.addAll(items);

            List<AbstractSpaceVisualizer<String, ?>> views = List.of(
                    new Space2DVisualizer<>(),
                    new Space3DVisualizer<>()
            );

            List<DistanceStrategy> strategies = List.of(
                    new EuclideanStrategy(),
                    new CosineStrategy(),
                    new ManhattanStrategy()
            );

            Function<String, String> parser = id -> id;
            List<SpaceCommand<String>> commands = List.of(
                    new KnnUI<>(space, vocabulary, parser),
                    new DistanceUI<>(space, vocabulary, parser),
                    new AnalogyUI<>(space, vocabulary, parser),
                    new CentroidUI<>(space, parser),
                    new SemanticLineUI<>(space, vocabulary, parser),
                    new RadiusUI<>(space, vocabulary, parser),
                    new MarkerUI<>(space, vocabulary, parser),
                    new CustomUI<>(space, vocabulary, parser)
            );

            AppUIManager<String> uiManager = new AppUIManager<>(space, strategies, views, commands, true);
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
}