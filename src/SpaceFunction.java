public interface SpaceFunction<T, R> {
    R execute(SpaceComponent<T> space, DistanceStrategy strategy);
}