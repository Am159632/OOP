package actions;

import java.util.Stack;

public class HistoryManager<T> {

    /** Maximum number of actions retained in the undo stack. */
    private static final int MAX_HISTORY_SIZE = 50;

    private final Stack<AppAction<T>> undoStack = new Stack<>();
    private final Stack<AppAction<T>> redoStack = new Stack<>();

    public void addAction(AppAction<T> action) {
        if (!undoStack.isEmpty() && undoStack.peek().equals(action)) {
            return;
        }
        undoStack.push(action);
        redoStack.clear();

        if (undoStack.size() > MAX_HISTORY_SIZE) {
            undoStack.remove(0);
        }
    }

    public AppAction<T> undo() {
        if (!undoStack.isEmpty()) {
            AppAction<T> action = undoStack.pop();
            action.undo();
            redoStack.push(action);
            return action;
        }
        return null;
    }

    public AppAction<T> redo() {
        if (!redoStack.isEmpty()) {
            AppAction<T> action = redoStack.pop();
            undoStack.push(action);
            return action;
        }
        return null;
    }

    public AppAction<T> peekUndo() {
        return undoStack.isEmpty() ? null : undoStack.peek();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}