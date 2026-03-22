import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Space2DVisualizer<T> extends AbstractSpaceVisualizer<T, Circle> {
    private Pane pane = new Pane();
    private Label hoverLabel;
    private final double WIDTH = 800;
    private final double HEIGHT = 700;
    private final double PADDING = 40;

    public Space2DVisualizer() {
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: transparent;");

        hoverLabel = new Label();
        hoverLabel.setVisible(false);
        hoverLabel.setStyle("-fx-background-color: rgba(110, 193, 255, 0.9); -fx-text-fill: #050814; -fx-padding: 4 8; -fx-font-weight: bold; -fx-background-radius: 4;");
        pane.getChildren().add(hoverLabel);
    }

    @Override
    protected Circle createShape(T id, double normX, double normY, double normZ) {
        double x = PADDING + normX * (WIDTH - 2 * PADDING);
        double y = HEIGHT - (PADDING + normY * (HEIGHT - 2 * PADDING));

        Circle circle = new Circle(x, y, 6);
        circle.setFill(Color.web(getDefaultColor()));

        circle.setOnMouseEntered(e -> {
            hoverLabel.setText(id.toString());
            hoverLabel.setLayoutX(x + 10);
            hoverLabel.setLayoutY(y - 25);
            hoverLabel.setVisible(true);
            hoverLabel.toFront();
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
        });

        circle.setOnMouseExited(e -> {
            hoverLabel.setVisible(false);
            circle.setStroke(null);
        });

        circle.setOnMouseClicked(e -> {
            if (onNodeClickedListener != null) {
                onNodeClickedListener.accept(id);
            }
        });

        return circle;
    }

    @Override
    protected void addShapeToScene(Circle shape) { pane.getChildren().add(shape); }

    @Override
    protected void applyColor(Circle shape, String colorHex) { shape.setFill(Color.web(colorHex)); }

    @Override
    protected String getDefaultColor() { return "#1f2a44"; }

    @Override
    protected void clearScene() {
        pane.getChildren().clear();
        pane.getChildren().add(hoverLabel);
    }

    @Override
    public Node getVisualNode() { return pane; }
}