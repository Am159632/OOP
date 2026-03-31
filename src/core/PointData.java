package core;

public class PointData<T> {
    private final T id;
    private final double[] coordinates;

    public PointData(T id, double[] coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }

    public T getId() { return id; }
    public double[] getCoordinates() { return coordinates; }
}