package extra;

public class Term<T> {
    public final boolean isAdd;
    public final T item;

    public Term(boolean isAdd, T item) {
        this.isAdd = isAdd;
        this.item = item;
    }
}