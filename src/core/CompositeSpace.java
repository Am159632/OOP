package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompositeSpace<T> implements SpaceComponent<T> {

    private String compositeName;
    private List<SpaceComponent<T>> children = new ArrayList<>();

    public CompositeSpace(String compositeName) {
        this.compositeName = compositeName;
    }

    public void addSpace(SpaceComponent<T> space) {
        children.add(space);
    }

    @Override
    public double[] getVector(String targetSpaceName, T id) {
        for (SpaceComponent<T> child : children) {
            double[] result = child.getVector(targetSpaceName, id);
            if (result != null) return result;
        }
        return null;
    }

    @Override
    public boolean containsItem(T id) {
        for (SpaceComponent<T> child : children) {
            if (child.containsItem(id)) return true;
        }
        return false;
    }

    @Override
    public Set<T> getItems(String targetSpaceName) {
        for (SpaceComponent<T> child : children) {
            Set<T> items = child.getItems(targetSpaceName);
            if (items != null) return items;
        }
        return null;
    }

}