package core;

import math.*;

import java.util.Set;

public abstract class AbstractAnalyzableSpace<T> implements AnalyzableSpace<T> {


    protected abstract SpaceComponent<T> getDataSpace();

    @Override
    public Set<T> getItems(String spaceName) {
        return getDataSpace().getItems(spaceName);
    }

    @Override
    public <R> R executeFunction(SpaceFunction<T, R> function, DistanceStrategy strategy) {
        return function.execute(getDataSpace(), strategy);
    }

    @Override
    public double[] getCoordinates(String spaceName, T id, int... axes) {
        double[] vector = getVector(spaceName, id);
        if (vector == null) return null;

        double[] result = new double[axes.length];
        for (int i = 0; i < axes.length; i++) {
            if (axes[i] >= 0 && axes[i] < vector.length) {
                result[i] = vector[axes[i]];
            } else {
                result[i] = 0.0;
            }
        }
        return result;
    }

    public double[] getVector(String spaceName, T id) {
        return getDataSpace().getVector(spaceName, id);
    }
}