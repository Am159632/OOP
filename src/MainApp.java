import core.OurSpace;
import extra.MarkerUI;
import extra.RadiusUI;
import extra.Space1DVisualizer;
import math.*;
import ui.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import visuals.AbstractSpaceVisualizer;
import visuals.Space2DVisualizer;
import visuals.Space3DVisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;

public class MainApp extends Application {

    private static OurSpace space;
    private static Map<String, DistanceStrategy> finalStrategies = new HashMap<>();
    private static List<AbstractSpaceVisualizer<String, ?>> finalViews = new ArrayList<>();
    private static List<SpaceCommand<String>> finalCommands = new ArrayList<>();
    private static boolean finalZoom = true;

    public static void main(String[] args) {
        System.out.println("Loading data... Please wait.");
        try {
            space = new OurSpace();
            space.loadFiles("C:/Users/asafm/IdeaProjects/OOP/full_vectors.json", "C:/Users/asafm/IdeaProjects/OOP/pca_vectors.json");
        } catch (Exception e) {
            System.out.println("Error loading files! Please check the paths.");
            e.printStackTrace();
            return;
        }

        List<String> vocabulary = new ArrayList<>();
        Set<String> items = space.getItems("FULL");
        if (items != null) vocabulary.addAll(items);

        finalStrategies = new HashMap<>();
        finalStrategies.put("Euclidean", new EuclideanStrategy());
        finalStrategies.put("Cosine", new CosineStrategy());

        finalViews = List.of(
                new Space2DVisualizer<>(),
                new Space3DVisualizer<>()
        );

        finalCommands = List.of(
                new KnnUI<>(space, vocabulary),
                new DistanceUI<>(space, vocabulary),
                new AnalogyUI<>(space, vocabulary),
                new CentroidUI<>(space),
                new SemanticLineUI<>(space, vocabulary),
                new RadiusUI<>(space,vocabulary),new MarkerUI<>(space,vocabulary)
        );

        System.out.println("\nStarting GUI...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            AppUIManager<String> uiManager = new AppUIManager<>(space, finalStrategies, finalViews, finalCommands, finalZoom);
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