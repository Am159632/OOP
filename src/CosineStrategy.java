public class CosineStrategy implements DistanceStrategy {
    @Override
    public double calculate(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("הוקטורים חייבים להיות באותו אורך!");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            normA += Math.pow(v1[i], 2);
            normB += Math.pow(v2[i], 2);
        }

        if (normA == 0 || normB == 0) return 1.0;

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));

        return 1.0 - similarity;
    }

    @Override
    public String toString(){
        return "Cosine distance";
    }
}