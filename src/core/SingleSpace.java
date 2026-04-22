package core;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SingleSpace<T> implements SpaceComponent<T> {

    private final String name;
    private final Map<T, double[]> data;

    public SingleSpace(String name, Map<T, double[]> data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public double[] getVector(String targetSpaceName, T id) {
        if (this.name.equals(targetSpaceName)) {
            return data.get(id);
        }
        return null;
    }

    @Override
    public boolean containsItem(T id) {
        return data.containsKey(id);
    }

    @Override
    public Set<T> getItems(String targetSpaceName) {
        if (this.name.equals(targetSpaceName)) {
            return data.keySet();
        }
        return Collections.emptySet();
    }

}