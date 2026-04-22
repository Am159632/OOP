package extra;

import actions.AbstractAnalysisAction;
import core.AbstractAnalyzableSpace;
import core.SpaceNames;
import math.DistanceStrategy;
import visuals.HighlightColors;
import visuals.SpaceVisualizer;

import java.util.List;
import java.util.stream.Collectors;

public class CustomAction<T> extends AbstractAnalysisAction<T> {
    private List<Term<T>> terms;
    private T result;

    public CustomAction(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, List<Term<T>> terms) {
        super(space, visualizer, strategy);
        this.terms = terms;
    }

    @Override
    public String execute() {
        if (!isCalculated) {
            CustomFunction<T> func = new CustomFunction<>(SpaceNames.FULL, terms);
            result = space.executeFunction(func, strategy);
            isCalculated = true;
        }

        if (result != null) {
            List<T> positives = terms.stream().filter(t -> t.isAdd).map(t -> t.item).collect(Collectors.toList());
            List<T> negatives = terms.stream().filter(t -> !t.isAdd).map(t -> t.item).collect(Collectors.toList());

            visualizer.highlightItems(positives, HighlightColors.PRIMARY);
            visualizer.highlightItems(negatives, HighlightColors.DANGER);
            visualizer.highlightItems(List.of(result), HighlightColors.SUCCESS);

            for (T p : positives) visualizer.drawLine(p, result, HighlightColors.PRIMARY, 1.5);
            for (T n : negatives) visualizer.drawLine(n, result, HighlightColors.DANGER, 1.5);

            StringBuilder sb = new StringBuilder("Equation: ");
            for (int i = 0; i < terms.size(); i++) {
                if (i > 0 || !terms.get(i).isAdd) {
                    sb.append(terms.get(i).isAdd ? "+ " : "- ");
                }
                sb.append(terms.get(i).item).append(" ");
            }
            sb.append("= ").append(result).append(" (Strategy: ").append(strategy.getName()).append(")");
            return sb.toString();
        }
        return "No valid result found.";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomAction<?> that = (CustomAction<?>) obj;

        List<String> thisPos = this.terms.stream().filter(t -> t.isAdd).map(t -> t.item.toString()).sorted().collect(Collectors.toList());
        List<String> thatPos = that.terms.stream().filter(t -> t.isAdd).map(t -> t.item.toString()).sorted().collect(Collectors.toList());
        List<String> thisNeg = this.terms.stream().filter(t -> !t.isAdd).map(t -> t.item.toString()).sorted().collect(Collectors.toList());
        List<String> thatNeg = that.terms.stream().filter(t -> !t.isAdd).map(t -> t.item.toString()).sorted().collect(Collectors.toList());

        return thisPos.equals(thatPos) && thisNeg.equals(thatNeg);
    }
}