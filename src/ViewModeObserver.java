public interface ViewModeObserver<T> {
    void onViewModeChanged(boolean is3D, SpaceVisualizer<T> activeVisualizer);
}