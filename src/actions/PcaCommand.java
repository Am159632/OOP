package actions;

import core.*;
import visuals.SpaceVisualizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PcaCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private int[] targetAxes;

    public PcaCommand(AbstractAnalyzableSpace<T> space, int... targetAxes) {
        this.space = space;
        this.targetAxes = targetAxes;
    }

    public String execute(SpaceVisualizer<T> visualizer) {
        try {
            Set<T> items = space.getItems(SpaceNames.PCA);
            if (items.isEmpty()) return "No items found in PCA space.";

            int dim = 0;
            for (T item : items) {
                double[] vector = space.getVector(SpaceNames.PCA, item);
                if (vector != null && vector.length > 0) {
                    dim = vector.length;
                    break;
                }
            }
            if (dim == 0) return "Error: Vectors are empty or invalid.";

            for (int i = 0; i < targetAxes.length; i++) {
                targetAxes[i] = (targetAxes[i] % dim + dim) % dim;
            }

            List<PointData<T>> pointsToDraw = new ArrayList<>();
            for (T item : items) {
                double[] vector = space.getVector(SpaceNames.PCA, item);
                if (vector != null && vector.length >= dim) {
                    double[] coords = new double[targetAxes.length];
                    for (int i = 0; i < targetAxes.length; i++) {
                        coords[i] = vector[targetAxes[i]];
                    }
                    pointsToDraw.add(new PointData<>(item, coords));
                }
            }

            visualizer.drawSpace(pointsToDraw);

            return "PCA Space loaded with axes: " + Arrays.toString(targetAxes);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}