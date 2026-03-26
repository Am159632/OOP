import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class Space3DVisualizer<T> extends AbstractSpaceVisualizer<T, Sphere> {
    private Group world = new Group();
    private Group cameraPivot = new Group();
    private Pane wrapper;
    private double mouseOldX, mouseOldY;
    private PerspectiveCamera camera;
    private Rotate cameraPitch;
    private Rotate cameraYaw;
    private Label hoverLabel;

    private final double SCENE_RANGE = 1200.0;
    private final double DEFAULT_RADIUS = 7.0;
    private final double HIGHLIGHT_RADIUS = 15.0;

    public Space3DVisualizer() {

        world.getChildren().add(new AmbientLight(Color.rgb(220, 220, 220)));

        PointLight headlamp = new PointLight(Color.WHITE);
        headlamp.setTranslateZ(-2500);
        cameraPivot.getChildren().add(headlamp);

        // מצלמה בציר (לוויין)
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(15000.0);
        camera.setTranslateZ(-2500);

        cameraPitch = new Rotate(0, Rotate.X_AXIS);
        cameraYaw = new Rotate(0, Rotate.Y_AXIS);

        cameraPivot.getTransforms().addAll(cameraYaw, cameraPitch);
        cameraPivot.getChildren().add(camera);
        world.getChildren().add(cameraPivot);

        // צירי עזר (אדום=X, ירוק=Y, כחול=Z)
        buildAxes();

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

    private void buildAxes() {
        Box xAxis = new Box(SCENE_RANGE, 2, 2); xAxis.setMaterial(new PhongMaterial(Color.RED));
        Box yAxis = new Box(2, SCENE_RANGE, 2); yAxis.setMaterial(new PhongMaterial(Color.GREEN));
        Box zAxis = new Box(2, 2, SCENE_RANGE); zAxis.setMaterial(new PhongMaterial(Color.BLUE));
        world.getChildren().addAll(xAxis, yAxis, zAxis);
    }

    @Override
    protected Sphere createShape(T id, double normX, double normY, double normZ) {
        Sphere sphere = new Sphere(DEFAULT_RADIUS, 12); // רזולוציה 12 לביצועים!

        sphere.setTranslateX((normX - 0.5) * SCENE_RANGE);
        sphere.setTranslateY((normY - 0.5) * SCENE_RANGE);
        sphere.setTranslateZ((normZ - 0.5) * SCENE_RANGE);

        PhongMaterial material = new PhongMaterial(Color.web(getDefaultColor()));
        sphere.setMaterial(material);

        sphere.setOnMouseEntered(e -> {
            sphere.setRadius(HIGHLIGHT_RADIUS);
            material.setDiffuseColor(Color.YELLOW);

            hoverLabel.setText(id.toString());
            hoverLabel.setLayoutX(e.getSceneX() + 15);
            hoverLabel.setLayoutY(e.getSceneY() - 30);
            hoverLabel.setVisible(true);
            hoverLabel.toFront();
        });

        sphere.setOnMouseExited(e -> {
            hoverLabel.setVisible(false);
            if (!currentlyHighlighted.contains(id)) {
                sphere.setRadius(DEFAULT_RADIUS);
                material.setDiffuseColor(Color.web(getDefaultColor()));
            } else {
                // אם הנקודה צבועה (בגלל פקודה), נחזיר לה את צבע ההדגשה הקודם שלה
                material.setDiffuseColor(Color.web(((PhongMaterial)sphere.getMaterial()).getDiffuseColor().toString()));
            }
        });

        return sphere;
    }

    @Override
    protected void addShapeToScene(Sphere shape) {
        world.getChildren().add(shape);
    }

    @Override
    protected void applyColor(Sphere shape, String colorHex) {
        PhongMaterial mat = (PhongMaterial) shape.getMaterial();
        mat.setDiffuseColor(Color.web(colorHex));
        shape.setRadius(HIGHLIGHT_RADIUS);
    }

    @Override
    protected String getDefaultColor() {
        return "#4a90e2";
    }

    @Override
    public void clearScene() {
        world.getChildren().removeIf(node -> node instanceof Sphere);
    }

    @Override
    public Node getVisualNode() {
        return wrapper;
    }
}