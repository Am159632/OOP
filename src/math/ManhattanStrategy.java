package math;

public class ManhattanStrategy implements DistanceStrategy {
    @Override
    public double calculate(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vectors must be of the same length!");
        }

        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            sum += Math.abs(v1[i] - v2[i]);
        }
        return sum;
    }

    @Override
    public String getName() {
        return "Manhattan distance";
    }

    @Override
    public String toString() {
        return getName();
    }
}