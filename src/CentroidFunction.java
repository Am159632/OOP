import java.util.List;

public class CentroidFunction<T> implements SpaceFunction<T, T> {
    private String spaceName;
    private List<T> group;

    public CentroidFunction(String spaceName, List<T> group) {
        this.spaceName = spaceName;
        this.group = group;
    }

    @Override
    public T execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        if (group == null || group.isEmpty()) return null;

        double[] first = space.getVector(spaceName, group.get(0));
        if (first == null) return null;

        double[] sumVec = new double[first.length];
        int count = 0;

        for (T id : group) {
            double[] v = space.getVector(spaceName, id);
            if (v != null) {
                for (int i = 0; i < v.length; i++) sumVec[i] += v[i];
                count++;
            }
        }

        if (count == 0) return null;

        // חלוקה בכמות כדי למצוא את הממוצע
        for (int i = 0; i < sumVec.length; i++) sumVec[i] /= count;

        T bestMatch = null;
        double minDistance = Double.MAX_VALUE;

        // מציאת המילה הכי קרובה לממוצע
        for (T id : space.getItems(spaceName)) {
            if (group.contains(id)) continue; // לא רוצים לבחור מילה מתוך הקבוצה עצמה

            double[] currentVec = space.getVector(spaceName, id);
            if (currentVec != null) {
                double dist = strategy.calculate(sumVec, currentVec);
                if (dist < minDistance) {
                    minDistance = dist;
                    bestMatch = id;
                }
            }
        }
        return bestMatch;
    }
}