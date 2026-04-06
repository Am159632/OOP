package math;

import core.SpaceComponent;
import extra.CustomFunction;
import extra.Term;

import java.util.Arrays;

public class AnalogyFunction<T> implements SpaceFunction<T, T> {
    private CustomFunction<T> customFunction;

    public AnalogyFunction(String spaceName, T w1, T w2, T w3) {
        this.customFunction = new CustomFunction<>(spaceName, Arrays.asList(
                new Term<>(true, w1),
                new Term<>(false, w2),
                new Term<>(true, w3)
        ));
    }

    @Override
    public T execute(SpaceComponent<T> space, DistanceStrategy strategy) {
        return customFunction.execute(space, strategy);
    }
}