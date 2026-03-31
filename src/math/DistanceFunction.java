package math;

import core.SpaceComponent;

public class DistanceFunction<T> implements SpaceFunction<T, Double> {
    private String spaceName;
    private T id1;
    private T id2;

    public DistanceFunction(String spaceName, T id1, T id2) {
        this.spaceName = spaceName;
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public Double execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        double[] v1 = space.getVector(spaceName, id1);
        double[] v2 = space.getVector(spaceName, id2);

        if (v1 == null || v2 == null) {
            throw new IllegalArgumentException("One or more items not found in space: " + spaceName);
        }
        return strategy.calculate(v1, v2);
    }
}