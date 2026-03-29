import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Space2DVisualizer<T> extends AbstractSpaceVisualizer<T, Circle> {

    private static final double DEFAULT_RADIUS = 3.0;
    private static final double HIGHLIGHT_RADIUS = 5.0;
    private static final double DEFAULT_OPACITY = 0.6;
    private static final double HIGHLIGHT_OPACITY = 1.0;

    private Pane pane = new Pane();
    private Label hoverLabel;
    private final double WIDTH = 800;
    private final double HEIGHT = 700;
    private final double PADDING = 40;
    private double mouseOldX, mouseOldY;

    public Space2DVisualizer() {
        super("2D Dimensional View");
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: transparent;");

        hoverLabel = new Label();
        hoverLabel.setVisible(false);
        hoverLabel.setStyle("-fx-background-color: rgba(110, 193, 255, 0.9); -fx-text-fill: #050814; -fx-padding: 4 8; -fx-font-weight: bold; -fx-background-radius: 4;");
        pane.getChildren().add(hoverLabel);

        pane.setOnMousePressed(e -> {
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

        pane.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - mouseOldX;
            double deltaY = e.getSceneY() - mouseOldY;

            pane.setTranslateX(pane.getTranslateX() + deltaX);
            pane.setTranslateY(pane.getTranslateY() + deltaY);

            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

    }

    @Override
    protected Circle createShape(T id, double normX, double normY, double normZ) {
        double x = PADDING + normX * (WIDTH - 2 * PADDING);
        double y = HEIGHT - (PADDING + normY * (HEIGHT - 2 * PADDING));

        Circle circle = new Circle(x, y, DEFAULT_RADIUS);
        circle.setFill(Color.web(getDefaultColor(), DEFAULT_OPACITY));

        circle.setOnMouseEntered(e -> {
            hoverLabel.setText(id.toString());
            hoverLabel.setLayoutX(x + 10);
            hoverLabel.setLayoutY(y - 25);
            hoverLabel.setVisible(true);
            hoverLabel.toFront();
            applyHighlight(circle, getDefaultColor());
        });

        circle.setOnMouseExited(e -> {
            hoverLabel.setVisible(false);
            if (highlightedColors.containsKey(id)) {
                applyHighlight(circle, highlightedColors.get(id));
            } else {
                removeHighlight(circle);
            }
        });

        return circle;
    }

    @Override
    protected void addShapeToScene(Circle shape) { pane.getChildren().add(shape); }

    @Override
    protected void applyHighlight(Circle shape, String colorHex) {
        shape.setFill(Color.web(colorHex, HIGHLIGHT_OPACITY));
        shape.setRadius(HIGHLIGHT_RADIUS);
    }

    @Override
    protected void removeHighlight(Circle shape) {
        shape.setFill(Color.web(getDefaultColor(), DEFAULT_OPACITY));
        shape.setRadius(DEFAULT_RADIUS);
    }

    @Override
    protected String getDefaultColor() { return "#4a90e2"; }

    @Override
    protected void removeDrawnLine(Node line) {
        pane.getChildren().remove(line);
    }

    @Override
    public void clearScene() {
        pane.getChildren().clear();
        pane.getChildren().add(hoverLabel);
    }

    @Override
    public Node getVisualNode() { return pane; }

    @Override
    public void setZoom(double percentage) {
        double scale = percentage / 75.0;
        getVisualNode().setScaleX(scale);
        getVisualNode().setScaleY(scale);
    }

    @Override
    public void drawLine(T source, T target, String colorHex, double thickness) {
        Circle sourceNode = nodesMap.get(source);
        Circle targetNode = nodesMap.get(target);

        if (sourceNode == null || targetNode == null) return;

        Line line = new Line(sourceNode.getCenterX(), sourceNode.getCenterY(), targetNode.getCenterX(), targetNode.getCenterY());
        line.setStroke(Color.web(colorHex));
        line.setStrokeWidth(thickness);
        line.setOpacity(0.5);

        drawnLines.add(line);
        pane.getChildren().add(0, line);
    }
}