public abstract class EmbeddingItemFactory<T> {

    public EmbeddedItem create(T id, double[] fullVector, double[] pcaVector) {

        if (id == null ) {
            throw new IllegalArgumentException("שגיאה: אי אפשר ליצור פריט ללא שם!");
        }

        EmbeddedItem item = createItem(id, fullVector, pcaVector);
        System.out.println("נוצר בהצלחה הפריט: " + item.getId());

        return item;
    }

    protected abstract EmbeddedItem createItem(T id, double[] fullVector, double[] pcaVector);
}