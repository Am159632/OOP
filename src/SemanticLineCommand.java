import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticLineCommand<T> implements SpaceCommand<T> {
    private AbstractAnalyzableSpace<T> space;
    private SpaceVisualizer<T> visualizer;
    private DistanceStrategy strategy;
    private T startId, endId;
    private int k; // התוספת הגאונית שלך!

    public SemanticLineCommand(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, T startId, T endId, int k) {
        this.space = space; this.visualizer = visualizer; this.strategy = strategy;
        this.startId = startId; this.endId = endId; this.k = k;
    }

    @Override
    public String execute() {
        List<ItemDistance<T>> projections = new ArrayList<>();

        // 1. מחשבים את ההיטל של כל מילה במרחב על הציר
        for (T item : space.getItems("FULL")) {
            // לא נכלול את מילות הציר עצמן בתוצאות כדי שיהיה מעניין
            if (item.equals(startId) || item.equals(endId)) continue;

            ProjectionFunction<T> func = new ProjectionFunction<>("FULL", item, startId, endId);
            double val = space.executeFunction(func, strategy);
            projections.add(new ItemDistance<>(item, val)); // ה-Distance פה שומר בעצם את ערך ההיטל
        }

        // 2. ממיינים את כל התוצאות מהמספר הכי קטן (קרוב להתחלה) להכי גדול (קרוב לסוף)
        projections.sort(Comparator.comparingDouble(ItemDistance::getDistance));

        // 3. חותכים את ה-K הקרובים להתחלה (startId)
        List<T> closeToStart = projections.stream()
                .limit(k)
                .map(ItemDistance::getId)
                .collect(Collectors.toList());

        // 4. חותכים את ה-K הקרובים לסוף (endId) - לוקחים מהסוף אחורה
        List<T> closeToEnd = projections.stream()
                .skip(Math.max(0, projections.size() - k))
                .map(ItemDistance::getId)
                .collect(Collectors.toList());
        // נהפוך את הרשימה השנייה כדי שהכי קרוב יופיע ראשון
        java.util.Collections.reverse(closeToEnd);

        // 5. צובעים! צד אחד באדום, צד שני בירוק
        visualizer.highlightItems(closeToStart, "#FF4500"); // כתום-אדום
        visualizer.highlightItems(closeToEnd, "#32CD32");   // ירוק-ליים

        // 6. בונים את הטקסט המרשים למסך
        StringBuilder sb = new StringBuilder();
        sb.append("ציר סמנטי: ").append(startId).append(" ◄──► ").append(endId).append("\n\n");

        sb.append("ה-").append(k).append(" הכי קרובים ל-'").append(startId).append("':\n");
        closeToStart.forEach(word -> sb.append("- ").append(word).append("\n"));

        sb.append("\nה-").append(k).append(" הכי קרובים ל-'").append(endId).append("':\n");
        closeToEnd.forEach(word -> sb.append("- ").append(word).append("\n"));

        return sb.toString();
    }

    @Override
    public void undo() {
        visualizer.clearHighlights();
    }
}