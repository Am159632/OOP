public interface AppAction<T> {
    String execute();
    void undo();
    String getName();
}