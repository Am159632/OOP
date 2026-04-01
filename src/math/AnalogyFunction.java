package math;

import core.ItemDistance;
import core.SpaceComponent;
import java.util.ArrayList;
import java.util.List;

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

        double[] resultVec = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            resultVec[i] = v1[i] - v2[i] + v3[i];
        }

        List<T> wordsToSkip = List.of(word1, word2, word3);

        List<ItemDistance<T>> closest = SimilaritySearcher.findKClosest(resultVec, wordsToSkip, space, strategy, spaceName, 1);

        return closest.isEmpty() ? null : closest.get(0).getId();
    }
}