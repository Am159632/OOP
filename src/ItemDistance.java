public class ItemDistance<T> {
    private final T id;
    private final double distance;

    public ItemDistance(T id, double distance) {
        this.id = id;
        this.distance = distance;
    }

    public T getId() { return id; }
    public double getDistance() { return distance; }
}