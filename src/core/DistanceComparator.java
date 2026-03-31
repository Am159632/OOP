package core;

import java.util.Comparator;

public class DistanceComparator<T> implements Comparator<ItemDistance<T>> {
    @Override
    public int compare(ItemDistance<T> a, ItemDistance<T> b) {
        return Double.compare(a.getDistance(), b.getDistance());
    }
}