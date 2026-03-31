package actions;

import java.util.Stack;

public class HistoryManager<T> {
    private final Stack<AppAction<T>> undoStack = new Stack<>();
    private final Stack<AppAction<T>> redoStack = new Stack<>();

    public void addAction(AppAction<T> action) {
        if (!undoStack.isEmpty() && undoStack.peek().equals(action)) {
            return;
        }
        undoStack.push(action);
        redoStack.clear();
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