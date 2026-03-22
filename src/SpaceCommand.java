// עכשיו זה גנרי, ומחזיר מחרוזת במקום להשתמש ב-Consumer!
public interface SpaceCommand<T> {
    String execute(); // מבצע את המתמטיקה והציור, ומחזיר טקסט למסך ה-UI
    void undo();      // מסיר רק את הציור של הפעולה הזו (מנקה אפקטים)
}