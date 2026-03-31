package core;

import math.*;
import actions.*;
import visuals.*;
import ui.*;

import java.util.Set;

public interface AnalyzableSpace<T> {
    <R> R executeFunction(SpaceFunction<T, R> function, DistanceStrategy strategy);
    Set<T> getItems(String spaceName);
    double[] getCoordinates(String spaceName, T id, int dimX, int dimY);
}