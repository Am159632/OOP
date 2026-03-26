public class GenericItem<T> implements EmbeddedItem<T> {
    private T id;
    private double[] fullVector;
    private double[] pcaVector;

    public GenericItem(T id, double[] fullVector, double[] pcaVector) {
        this.id = id;
        this.fullVector = fullVector;
        this.pcaVector = pcaVector;
    }

    @Override
    public T getId() {
        return id;
    }

    @Override
    public double[] getFullVector() {
        return fullVector;
    }

    @Override
    public double[] getPcaVector() {
        return pcaVector;
    }
}