import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PcaCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private int dimX, dimY, dimZ;

    public PcaCommand(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, int dimX, int dimY, int dimZ) {
        this.space = space; this.visualizer = visualizer;
        this.dimX = dimX; this.dimY = dimY; this.dimZ = dimZ;
    }

    @Override
    public String execute() {
        Set<T> items = space.getItems("PCA");
        List<PointData<T>> points = new ArrayList<>();

        for (T item : items) {
            double[] fullVec = space.getVector("PCA", item);
            if (fullVec != null && fullVec.length > 0) {
                int len = fullVec.length;
                // מודולו שומר עלינו מקריסה אם המשתמש הכניס מספר גדול מדי
                double x = fullVec[Math.abs(dimX) % len];
                double y = fullVec[Math.abs(dimY) % len];
                double z = fullVec[Math.abs(dimZ) % len];
                points.add(new PointData<>(item, new double[]{x, y, z}));
            }
        }
        visualizer.drawSpace(points);
        return "מרחב ה-PCA נטען בהצלחה! צוירו " + points.size() + " נקודות למסך.";
    }

    @Override
    public void undo() { visualizer.clearSpace(); }
}