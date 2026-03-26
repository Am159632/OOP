import javafx.scene.Node;

public interface SpaceCommand<T> {
    String getName();
    Node getUI();
    void setStrategy(DistanceStrategy strategy);
    void onNodeClicked(T item);
    AppAction<T> generateAction(SpaceVisualizer<T> visualizer);
}