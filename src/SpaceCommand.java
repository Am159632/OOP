public interface SpaceCommand<T> {
    String getName();
    javafx.scene.Node getUI();
    void setStrategy(DistanceStrategy strategy);
    void onNodeClicked(T item);

    String execute(SpaceVisualizer<T> visualizer);
    void undo(SpaceVisualizer<T> visualizer);
}