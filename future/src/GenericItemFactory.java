public class GenericItemFactory<T> extends EmbeddingItemFactory<T> {

    @Override
    protected EmbeddedItem<T> createItem(T id, double[] fullVector, double[] pcaVector) {
        return new GenericItem<>(id, fullVector, pcaVector);
    }
}