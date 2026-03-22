import java.util.Comparator;

public class DistanceComparator<T> implements Comparator<ItemDistance<T>> {
    @Override
    public int compare(ItemDistance<T> a, ItemDistance<T> b) {
        // משווה בין המרחקים של שני האובייקטים
        return Double.compare(a.getDistance(), b.getDistance());
    }
}