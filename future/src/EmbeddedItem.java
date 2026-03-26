public interface EmbeddedItem<T> {
    T getId(); //מזהה הוקטור
    double[] getFullVector();//וקטור השלם
    double[] getPcaVector();//וקטור מכווץ
}

