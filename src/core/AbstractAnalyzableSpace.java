package core;

import math.*;
import actions.*;
import visuals.*;
import ui.*;

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
    public double[] getCoordinates(String spaceName, T id, int dimX, int dimY) {
        SpaceComponent<T> space = getDataSpace();
        double[] vector = space.getVector(spaceName, id);
        if (vector == null || dimX >= vector.length || dimY >= vector.length) return null;
        return new double[]{vector[dimX], vector[dimY]};
    }

    // הוספנו את זה כדי למשוך וקטור שלם במקום רק קואורדינטות ספציפיות
    public double[] getVector(String spaceName, T id) {
        return getDataSpace().getVector(spaceName, id);
    }
}