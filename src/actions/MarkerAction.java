package actions;

import visuals.SpaceVisualizer;
import java.util.List;

public class MarkerAction<T> implements AppAction<T> {
    private SpaceVisualizer<T> visualizer;
    private List<T> itemsToMark;
    private String colorHex;

    public MarkerAction(SpaceVisualizer<T> visualizer, List<T> itemsToMark, String colorHex) {
        this.visualizer = visualizer;
        this.itemsToMark = itemsToMark;
        this.colorHex = colorHex;
    }

    @Override
    public String execute() {
        visualizer.highlightItems(itemsToMark, colorHex);
        return "Marked items " + itemsToMark + " with color " + colorHex;
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MarkerAction<?> that = (MarkerAction<?>) obj;
        return this.itemsToMark.equals(that.itemsToMark) && this.colorHex.equals(that.colorHex);
    }
}