package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositeSpace<T> implements SpaceComponent<T> {

    private String compositeName;
    private Map<String, SpaceComponent<T>> childrenByName = new HashMap<>();

    public CompositeSpace(String compositeName) {
        this.compositeName = compositeName;
    }

    public void addSpace(String spaceName, SpaceComponent<T> space) {
        childrenByName.put(spaceName, space);
    }

    @Override
    public double[] getVector(String targetSpaceName, T id) {
        SpaceComponent<T> space = childrenByName.get(targetSpaceName);
        return space != null ? space.getVector(targetSpaceName, id) : null;
    }

    @Override
    public boolean containsItem(T id) {
        for (SpaceComponent<T> space : childrenByName.values()) {
            if (space.containsItem(id)) return true;
        }
        return false;
    }

    @Override
    public Set<T> getItems(String targetSpaceName) {
        SpaceComponent<T> space = childrenByName.get(targetSpaceName);
        return space != null ? space.getItems(targetSpaceName) : null;
    }

}