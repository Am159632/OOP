package math;

import core.SpaceComponent;

public class AnalogyFunction<T> implements SpaceFunction<T, T> {
    private String spaceName;
    private T word1, word2, word3;

    public AnalogyFunction(String spaceName, T word1, T word2, T word3) {
        this.spaceName = spaceName;
        this.word1 = word1;
        this.word2 = word2;
        this.word3 = word3;
    }

    @Override
    public T execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        double[] v1 = space.getVector(spaceName, word1);
        double[] v2 = space.getVector(spaceName, word2);
        double[] v3 = space.getVector(spaceName, word3);

        if (v1 == null || v2 == null || v3 == null) return null;

        // חישוב המשוואה: v1 - v2 + v3
        double[] resultVec = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            resultVec[i] = v1[i] - v2[i] + v3[i];
        }

        T bestMatch = null;
        double minDistance = Double.MAX_VALUE;

        // מציאת המילה הכי קרובה לתוצאה
        for (T currentId : space.getItems(spaceName)) {
            // לא רוצים שהתשובה תהיה אחת ממילות השאלה
            if (currentId.equals(word1) || currentId.equals(word2) || currentId.equals(word3)) continue;

            double[] currentVec = space.getVector(spaceName, currentId);
            if (currentVec != null) {
                double dist = strategy.calculate(resultVec, currentVec);
                if (dist < minDistance) {
                    minDistance = dist;
                    bestMatch = currentId;
                }
            }
        }
        return bestMatch;
    }
}