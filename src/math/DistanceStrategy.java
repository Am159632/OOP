package math;

public interface DistanceStrategy {
    double calculate(double[] v1, double[] v2);

    /** Human-readable name used for display (e.g. in combo boxes and result output). */
    String getName();
}
