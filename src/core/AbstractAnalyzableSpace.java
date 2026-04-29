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


    public double[] getVector(String spaceName, T id) {
        return getDataSpace().getVector(spaceName, id);
    }
}