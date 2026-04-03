package math;

import core.SpaceComponent;
import java.util.ArrayList;
import java.util.List;

public class RadiusFunction<T> implements SpaceFunction<T, List<T>> {
    private String spaceName;
    private T targetId;
    private double maxRadius;

    public RadiusFunction(String spaceName, T targetId, double maxRadius) {
        this.spaceName = spaceName;
        this.targetId = targetId;
        this.maxRadius = maxRadius;
    }

    @Override
    public List<T> execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        double[] targetVector = space.getVector(spaceName, targetId);
        if (targetVector == null) throw new IllegalArgumentException("Target not found");

        List<T> itemsInRadius = new ArrayList<>();

        for (T currentId : space.getItems(spaceName)) {
            if (currentId.equals(targetId)) continue;

            double[] currentVec = space.getVector(spaceName, currentId);
            if (currentVec != null) {
                double dist = strategy.calculate(targetVector, currentVec);
                if (dist <= maxRadius) {
                    itemsInRadius.add(currentId);
                }
            }
        }
        return itemsInRadius;
    }
}