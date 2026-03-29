import java.util.Set;

public class PcaCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private int pcX, pcY, pcZ; // הגרפיקה נמחקה מפה!

    // הגרפיקה נמחקה גם מהבנאי!
    public PcaCommand(AbstractAnalyzableSpace<T> space, int pcX, int pcY, int pcZ) {
        this.space = space;
        this.pcX = pcX;
        this.pcY = pcY;
        this.pcZ = pcZ;
    }

    public String execute(SpaceVisualizer<T> visualizer) {
        try {
            visualizer.clearScene();

            Set<T> items = space.getItems("PCA");
            if (items == null || items.isEmpty()) return "No items found.";

            int dim = 0;
            for (T item : items) {
                double[] vector = space.getVector("PCA", item);
                if (vector != null && vector.length > 0) {
                    dim = vector.length;
                    break;
                }
            }
            if (dim == 0) return "Error: Vectors are empty or invalid.";

            pcX = (pcX % dim + dim) % dim;
            pcY = (pcY % dim + dim) % dim;

            boolean is2D = (pcZ == Integer.MIN_VALUE);

            if (!is2D) {
                pcZ = (pcZ % dim + dim) % dim;
            }

            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

            for (T item : items) {
                double[] vector = space.getVector("PCA", item);
                if (vector != null && vector.length >= dim) {
                    minX = Math.min(minX, vector[pcX]); maxX = Math.max(maxX, vector[pcX]);
                    minY = Math.min(minY, vector[pcY]); maxY = Math.max(maxY, vector[pcY]);
                    if (pcZ >= 0) { minZ = Math.min(minZ, vector[pcZ]); maxZ = Math.max(maxZ, vector[pcZ]); }
                }
            }

            for (T item : items) {
                double[] vector = space.getVector("PCA", item);
                if (vector != null && vector.length >= dim) {
                    double normX = (maxX == minX) ? 0.5 : (vector[pcX] - minX) / (maxX - minX);
                    double normY = (maxY == minY) ? 0.5 : (vector[pcY] - minY) / (maxY - minY);
                    double normZ = (!is2D) ? ((maxZ == minZ) ? 0.5 : (vector[pcZ] - minZ) / (maxZ - minZ)) : 0.5;

                    visualizer.drawNode(item, normX, normY, normZ);
                }
            }

            String zText = (pcZ >= 0) ? (", Z=" + pcZ) : "";
            return "PCA Space loaded: X=" + pcX + ", Y=" + pcY + zText;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}