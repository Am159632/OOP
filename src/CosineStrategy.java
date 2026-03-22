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

        if (normA == 0 || normB == 0) return 1.0; // הגנה מפני חלוקה באפס

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));

        // ככל שהדמיון (Similarity) קרוב ל-1, המרחק יהיה קרוב ל-0 (כלומר קרובים מאוד!)
        return 1.0 - similarity;
    }
}