import javafx.scene.control.TextField;
import java.util.List;
import java.util.function.Consumer;

public class PcaCommandPanel<T> extends AbstractCommandPanel<T> {
    private TextField txtX, txtY, txtZ;
    private boolean isCurrently3D = false;

    public PcaCommandPanel(AbstractAnalyzableSpace<T> space, SpaceVisualizer<T> visualizer, DistanceStrategy strategy, Consumer<String> logger, List<T> vocabulary) {
        super(space, visualizer, strategy, logger, vocabulary);
        txtX = new TextField("0");
        txtY = new TextField("1");
        txtZ = new TextField("2");
        buildPanel();
    }

    @Override
    public String getCommandName() {
        return "טען מרחב (PCA)";
    }

    @Override
    protected void buildInputs() {
        txtX.setPromptText("ציר X");
        txtY.setPromptText("ציר Y");
        txtZ.setPromptText("ציר Z");
        panelRoot.getChildren().addAll(txtX, txtY);
        if (isCurrently3D) {
            panelRoot.getChildren().add(txtZ);
        }
    }

    @Override
    public void executeCommand() {
        try {
            int x = Integer.parseInt(txtX.getText());
            int y = Integer.parseInt(txtY.getText());
            int z = isCurrently3D ? Integer.parseInt(txtZ.getText()) : 0;
            currentVisualizer.clearHighlights();
            String res = new PcaCommand<>(space, currentVisualizer, x, y, z).execute();
            consoleLogger.accept(res);
        } catch (Exception e) {
            consoleLogger.accept("שגיאה בביצוע PCA.");
        }
    }

    @Override
    public void onViewModeChanged(boolean is3D, SpaceVisualizer<T> activeVisualizer) {
        super.onViewModeChanged(is3D, activeVisualizer);
        this.isCurrently3D = is3D;
        buildPanel();
        executeCommand();
    }

    @Override
    public void onNodeClicked(T item) {
    }
}