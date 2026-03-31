package math;

public class EuclideanStrategy implements DistanceStrategy {
    @Override
    public double calculate(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("הוקטורים חייבים להיות באותו אורך!");
        }

        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            sum += Math.pow(v1[i] - v2[i], 2);
        }
        return Math.sqrt(sum);
    }

    @Override
    public String toString(){
        return "Euclidean distance";
    }
}