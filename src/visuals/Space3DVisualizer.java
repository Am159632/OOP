package visuals;

import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class Space3DVisualizer<T> extends AbstractSpaceVisualizer<T, Sphere> {

    private static final double SCENE_RANGE = 1200.0;
    private static final double DEFAULT_RADIUS = 7.0;
    private static final double HIGHLIGHT_RADIUS = 15.0;
    private static final String HOVER_COLOR = "#FFFF00";

    private Group world = new Group();
    private Group cameraPivot = new Group();
    private Pane wrapper;
    private double mouseOldX, mouseOldY;
    private PerspectiveCamera camera;
    private Rotate cameraPitch;
    private Rotate cameraYaw;
    private Label hoverLabel;

    public Space3DVisualizer() {
        super("3D Dimensional View");
        world.getChildren().add(new AmbientLight(Color.rgb(220, 220, 220)));

        PointLight headlamp = new PointLight(Color.WHITE);
        headlamp.setTranslateZ(-2500);
        cameraPivot.getChildren().add(headlamp);

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(30000.0);
        camera.setTranslateZ(-2500);

        cameraPitch = new Rotate(0, Rotate.X_AXIS);
        cameraYaw = new Rotate(0, Rotate.Y_AXIS);

        cameraPivot.getTransforms().addAll(cameraYaw, cameraPitch);
        cameraPivot.getChildren().add(camera);
        world.getChildren().add(cameraPivot);

        SubScene subScene = new SubScene(world, 800, 700, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.rgb(20, 25, 40));

        wrapper = new Pane(subScene);
        subScene.widthProperty().bind(wrapper.widthProperty());
        subScene.heightProperty().bind(wrapper.heightProperty());

        hoverLabel = new Label();
        hoverLabel.setVisible(false);
        hoverLabel.setMouseTransparent(true);
        hoverLabel.setStyle("-fx-background-color: rgba(110, 193, 255, 0.9); -fx-text-fill: #050814; -fx-padding: 4 8; -fx-font-weight: bold; -fx-background-radius: 4;");
        wrapper.getChildren().add(hoverLabel);

        wrapper.setOnMousePressed(e -> {
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

        wrapper.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - mouseOldX;
            double deltaY = e.getSceneY() - mouseOldY;
            cameraYaw.setAngle(cameraYaw.getAngle() + deltaX * 0.2);
            cameraPitch.setAngle(cameraPitch.getAngle() - deltaY * 0.2);
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

        wrapper.setOnScroll(e -> camera.setTranslateZ(camera.getTranslateZ() + e.getDeltaY() * 10));
    }

    @Override
    protected Sphere createShape(T id, double normX, double normY, double normZ) {
        Sphere sphere = new Sphere(DEFAULT_RADIUS, 12);

        sphere.setTranslateX((normX - 0.5) * SCENE_RANGE);
        sphere.setTranslateY((normY - 0.5) * SCENE_RANGE);
        sphere.setTranslateZ((normZ - 0.5) * SCENE_RANGE);

        PhongMaterial material = new PhongMaterial(Color.web(getDefaultColor()));
        sphere.setMaterial(material);

        sphere.setOnMouseEntered(e -> {
            applyHighlight(sphere, HOVER_COLOR);

            hoverLabel.setText(id.toString());
            hoverLabel.setLayoutX(e.getSceneX() + 15);
            hoverLabel.setLayoutY(e.getSceneY() - 30);
            hoverLabel.setVisible(true);
            hoverLabel.toFront();
        });

        sphere.setOnMouseExited(e -> {
            hoverLabel.setVisible(false);
            if (highlightedColors.containsKey(id)) {
                applyHighlight(sphere, highlightedColors.get(id));
            } else {
                removeHighlight(sphere);
            }
        });

        return sphere;
    }

    @Override
    protected void addShapeToScene(Sphere shape) {
        world.getChildren().add(shape);
    }

    @Override
    protected void applyHighlight(Sphere shape, String colorHex) {
        PhongMaterial mat = (PhongMaterial) shape.getMaterial();
        mat.setDiffuseColor(Color.web(colorHex));
        shape.setRadius(HIGHLIGHT_RADIUS);
    }

    @Override
    protected void removeHighlight(Sphere shape) {
        PhongMaterial mat = (PhongMaterial) shape.getMaterial();
        mat.setDiffuseColor(Color.web(getDefaultColor()));
        shape.setRadius(DEFAULT_RADIUS);
    }

    @Override
    protected String getDefaultColor() {
        return "#4a90e2";
    }

    @Override
    protected void removeDrawnLine(Node line) {
        world.getChildren().remove(line);
    }

    @Override
    public void clearScene() {
        world.getChildren().removeIf(node -> node instanceof Sphere || node instanceof Cylinder);
    }

    @Override
    public Node getVisualNode() {
        return wrapper;
    }

    @Override
    public void setZoom(double percentage) {
        if (camera != null) {
            double zPos = -3000.0 + (percentage * 20.0);
            camera.setTranslateZ(zPos);
        }
    }

    @Override
    public void drawLine(T source, T target, String colorHex, double thickness) {
        Sphere sourceNode = nodesMap.get(source);
        Sphere targetNode = nodesMap.get(target);

        if (sourceNode == null || targetNode == null) return;

        Point3D start = new Point3D(sourceNode.getTranslateX(), sourceNode.getTranslateY(), sourceNode.getTranslateZ());
        Point3D end = new Point3D(targetNode.getTranslateX(), targetNode.getTranslateY(), targetNode.getTranslateZ());

        Point3D diff = end.subtract(start);
        Point3D mid = start.midpoint(end);

        Cylinder line = new Cylinder(thickness * 0.5, diff.magnitude());
        line.setTranslateX(mid.getX());
        line.setTranslateY(mid.getY());
        line.setTranslateZ(mid.getZ());

        PhongMaterial material = new PhongMaterial(Color.web(colorHex));
        line.setMaterial(material);

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotate = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        line.getTransforms().add(rotate);

        drawnLines.add(line);
        world.getChildren().add(line);
    }
}