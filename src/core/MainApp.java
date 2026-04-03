package core;

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

    private static boolean getUserConfirmation(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes") || input.isEmpty()) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("  -> Invalid input! Please enter 'y' for Yes, 'n' for No, or just press Enter.");
            }
        }
    }

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

        Map<String, DistanceStrategy> defaultStrategies = new HashMap<>();
        defaultStrategies.put("Euclidean", new EuclideanStrategy());
        defaultStrategies.put("Cosine", new CosineStrategy());

        List<AbstractSpaceVisualizer<String, ?>> defaultViews = List.of(
                new Space1DVisualizer<>(),
                new Space2DVisualizer<>(),
                new Space3DVisualizer<>()
        );

        List<SpaceCommand<String>> defaultCommands = List.of(
                new KnnUI<>(space, vocabulary),
                new DistanceUI<>(space, vocabulary),
                new AnalogyUI<>(space, vocabulary),
                new CentroidUI<>(space),
                new SemanticLineUI<>(space, vocabulary),
                new RadiusUI<>(space,vocabulary),new MarkerUI<>(space,vocabulary)
        );

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=========================================");
        System.out.println("   Welcome to Word Embedding Builder!    ");
        System.out.println("=========================================\n");

        boolean useDefault = getUserConfirmation(scanner, "Run default setup with all features?");

        if (!useDefault) {
            System.out.println("\n--- 1. Select Spaces (Views) ---");
            for (AbstractSpaceVisualizer<String, ?> view : defaultViews) {
                if (getUserConfirmation(scanner, "Include " + view.toString() + "?")) {
                    finalViews.add(view);
                }
            }

            System.out.println("\n--- 2. Select Distance Strategies ---");
            for (Map.Entry<String, DistanceStrategy> entry : defaultStrategies.entrySet()) {
                if (getUserConfirmation(scanner, "Include " + entry.getKey() + " Strategy?")) {
                    finalStrategies.put(entry.getKey(), entry.getValue());
                }
            }

            System.out.println("\n--- 3. Select Commands & Functions ---");
            if (finalStrategies.isEmpty()) {
                System.out.println("  -> Skipping: No distance strategies selected. Commands require at least one strategy.");
            } else {
                for (SpaceCommand<String> cmd : defaultCommands) {
                    if (getUserConfirmation(scanner, "Include " + cmd.getName() + " function?")) {
                        finalCommands.add(cmd);
                    }
                }
            }

            System.out.println("\n--- 4. General Settings ---");
            finalZoom = getUserConfirmation(scanner, "Enable Zoom feature?");

        } else {
            finalStrategies.putAll(defaultStrategies);
            finalViews.addAll(defaultViews);
            finalCommands.addAll(defaultCommands);
            finalZoom = true;
        }

        if (finalViews.isEmpty()) {
            System.out.println("\nWarning: No spaces selected! Defaulting to 3D Space.");
            finalViews.add(new Space3DVisualizer<>());
        }

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