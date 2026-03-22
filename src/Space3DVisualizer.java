import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class Space3DVisualizer<T> extends AbstractSpaceVisualizer<T, Sphere> {
    private Group world = new Group();
    private Pane wrapper;
    private double mouseOldX, mouseOldY;
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Label hoverLabel;

    public Space3DVisualizer() {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-2200);
        camera.setNearClip(0.1);
        camera.setFarClip(15000.0);

        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-1000);
        light.setTranslateY(-500);

        Bloom bloom = new Bloom();
        bloom.setThreshold(0.3);
        world.setEffect(bloom);

        world.getChildren().addAll(light, new AmbientLight(Color.rgb(100, 100, 100)));
        world.getTransforms().addAll(rotateY, rotateX);

        SubScene subScene = new SubScene(world, 800, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        wrapper = new Pane(subScene);
        subScene.widthProperty().bind(wrapper.widthProperty());
        subScene.heightProperty().bind(wrapper.heightProperty());

        hoverLabel = new Label();
        hoverLabel.setVisible(false);
        hoverLabel.setStyle("-fx-background-color: rgba(110, 193, 255, 0.9); -fx-text-fill: #050814; -fx-padding: 4 8; -fx-font-weight: bold; -fx-background-radius: 4;");
        wrapper.getChildren().add(hoverLabel);

        wrapper.setOnMousePressed(e -> {
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

        wrapper.setOnMouseDragged(e -> {
            rotateY.setAngle(rotateY.getAngle() + (e.getSceneX() - mouseOldX) * 0.2);
            rotateX.setAngle(rotateX.getAngle() - (e.getSceneY() - mouseOldY) * 0.2);
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

        wrapper.setOnScroll(e -> camera.setTranslateZ(camera.getTranslateZ() + e.getDeltaY() * 4));
    }

    @Override
    protected Sphere createShape(T id, double normX, double normY, double normZ) {
        Sphere sphere = new Sphere(9);
        sphere.setTranslateX((normX - 0.5) * 1200);
        sphere.setTranslateY((normY - 0.5) * 1200);
        sphere.setTranslateZ((normZ - 0.5) * 1200);

        PhongMaterial material = new PhongMaterial(Color.web(getDefaultColor()));
        material.setSpecularColor(Color.WHITE);
        material.setSpecularPower(32);
        sphere.setMaterial(material);

        sphere.setOnMouseEntered(e -> {
            sphere.setScaleX(1.8);
            sphere.setScaleY(1.8);
            sphere.setScaleZ(1.8);
            material.setSpecularColor(Color.YELLOW);
            hoverLabel.setText(id.toString());
            hoverLabel.setLayoutX(e.getSceneX() + 15);
            hoverLabel.setLayoutY(e.getSceneY() - 30);
            hoverLabel.setVisible(true);
            hoverLabel.toFront();
        });

        sphere.setOnMouseExited(e -> {
            sphere.setScaleX(1);
            sphere.setScaleY(1);
            sphere.setScaleZ(1);
            material.setSpecularColor(Color.WHITE);
            hoverLabel.setVisible(false);
        });

        sphere.setOnMouseClicked(e -> {
            if (onNodeClickedListener != null) {
                onNodeClickedListener.accept(id);
            }
        });

        return sphere;
    }

    @Override
    protected void addShapeToScene(Sphere shape) { world.getChildren().add(shape); }

    @Override
    protected void applyColor(Sphere shape, String colorHex) { ((PhongMaterial) shape.getMaterial()).setDiffuseColor(Color.web(colorHex)); }

    @Override
    protected String getDefaultColor() { return "#1f2a44"; }

    @Override
    protected void clearScene() { world.getChildren().removeIf(node -> node instanceof Sphere); }

    @Override
    public Node getVisualNode() { return wrapper; }
}