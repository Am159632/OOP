package core;

import java.util.Set;

public interface SpaceComponent<T> {

    double[] getVector(String spaceName, T id);

    boolean containsItem(T id);

    Set<T> getItems(String spaceName);

    void printSpaceInfo();

}