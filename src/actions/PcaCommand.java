package actions;

import core.*;
import visuals.*;
import java.util.Arrays;
import java.util.Set;

public class PcaCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private int[] targetAxes; // הכל הופך למערך דינמי אחד!

    // הבנאי עכשיו מקבל מערך של צירים (varargs)
    public PcaCommand(AbstractAnalyzableSpace<T> space, int... targetAxes) {
        this.space = space;
        this.targetAxes = targetAxes;
    }

    public String execute(SpaceVisualizer<T> visualizer) {
        try {
            // תיקון Decoupling: מנקים את כל החלל כולל המילונים, ולא רק את המסך הגרפי
            if (visualizer instanceof AbstractSpaceVisualizer) {
                ((AbstractSpaceVisualizer<?, ?>) visualizer).clearSpace();
            } else {
                visualizer.clearScene();
            }

            Set<T> items = space.getItems("PCA");
            if (items == null || items.isEmpty()) return "No items found.";

            // מציאת גודל הוקטור (המימד האמיתי של הנתונים)
            int dim = 0;
            for (T item : items) {
                double[] vector = space.getVector("PCA", item);
                if (vector != null && vector.length > 0) {
                    dim = vector.length;
                    break;
                }
            }
            if (dim == 0) return "Error: Vectors are empty or invalid.";

            // תיקון מתמטי: מסדרים רק את הצירים שבאמת קיבלנו! אין יותר Integer.MIN_VALUE
            for (int i = 0; i < targetAxes.length; i++) {
                targetAxes[i] = (targetAxes[i] % dim + dim) % dim;
            }

            // מערכים דינמיים לחישוב מינימום ומקסימום - DRY במיטבו
            double[] mins = new double[targetAxes.length];
            double[] maxs = new double[targetAxes.length];
            Arrays.fill(mins, Double.MAX_VALUE);
            Arrays.fill(maxs, -Double.MAX_VALUE);

            for (T item : items) {
                double[] vector = space.getVector("PCA", item);
                if (vector != null && vector.length >= dim) {
                    for (int i = 0; i < targetAxes.length; i++) {
                        mins[i] = Math.min(mins[i], vector[targetAxes[i]]);
                        maxs[i] = Math.max(maxs[i], vector[targetAxes[i]]);
                    }
                }
            }

            // נרמול וציור
            for (T item : items) {
                double[] vector = space.getVector("PCA", item);
                if (vector != null && vector.length >= dim) {
                    // אם אין ציר (למשל במימד 1), הוא אוטומטית מקבל 0.5 (ממורכז) בלי חישובי זבל
                    double normX = targetAxes.length > 0 ? normalize(vector[targetAxes[0]], mins[0], maxs[0]) : 0.5;
                    double normY = targetAxes.length > 1 ? normalize(vector[targetAxes[1]], mins[1], maxs[1]) : 0.5;
                    double normZ = targetAxes.length > 2 ? normalize(vector[targetAxes[2]], mins[2], maxs[2]) : 0.5;

                    visualizer.drawNode(item, normX, normY, normZ);
                }
            }

            return "PCA Space loaded with axes: " + Arrays.toString(targetAxes);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private double normalize(double val, double min, double max) {
        return (max == min) ? 0.5 : (val - min) / (max - min);
    }
}