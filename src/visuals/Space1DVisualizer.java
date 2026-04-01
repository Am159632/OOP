package visuals;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Space1DVisualizer<T> extends AbstractSpaceVisualizer<T, Circle> {

    private static final double DEFAULT_RADIUS = 4.0;
    private static final double HIGHLIGHT_RADIUS = 7.0;
    private static final double DEFAULT_OPACITY = 0.7;
    private static final double HIGHLIGHT_OPACITY = 1.0;

    private Pane pane = new Pane();
    private final double WIDTH = 800;
    private final double HEIGHT = 700;
    private final double PADDING = 40;
    private double mouseOldX;

    public Space1DVisualizer() {
        super("1D Dimensional View",1);
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: transparent;");

        drawBaseAxis();
        pane.getChildren().add(hoverLabel);

        pane.setOnMousePressed(e -> mouseOldX = e.getSceneX());
        pane.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - mouseOldX;
            pane.setTranslateX(pane.getTranslateX() + deltaX);
            mouseOldX = e.getSceneX();
        });

        pane.setOnScroll(e -> updateZoom(currentZoom + (e.getDeltaY() > 0 ? 5 : -5)));
    }

    private void drawBaseAxis() {
        Line axisLine = new Line(PADDING, HEIGHT / 2, WIDTH - PADDING, HEIGHT / 2);
        axisLine.setStroke(Color.LIGHTGRAY);
        axisLine.setStrokeWidth(2.0);
        pane.getChildren().add(axisLine);
    }

    @Override
    protected Circle createShape(T id, double normX, double normY, double normZ) {
        double x = PADDING + normX * (WIDTH - 2 * PADDING);
        double y = HEIGHT / 2;
        Circle circle = new Circle(x, y, DEFAULT_RADIUS);
        circle.setFill(Color.web(getDefaultColor(), DEFAULT_OPACITY));
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
    protected String getDefaultColor() { return "#9b59b6"; }

    @Override
    protected void removeDrawnLine(Node line) { pane.getChildren().remove(line); }

    @Override
    public void clearScene() {
        pane.getChildren().clear();
        drawBaseAxis();
        pane.getChildren().add(hoverLabel);
    }

    @Override
    public Node getVisualNode() { return pane; }

    @Override
    public void setZoom(double percentage) {
        this.currentZoom = percentage;
        getVisualNode().setScaleX(percentage / 50.0);
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
        pane.getChildren().add(1, line);
    }
}