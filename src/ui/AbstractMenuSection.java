package ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public abstract class AbstractMenuSection implements MenuSection {
    private String title;

    public AbstractMenuSection(String title) {
        this.title = title;
    }

    @Override
    public VBox build() {
        VBox box = new VBox(10);
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("section-title");

        box.getChildren().add(lblTitle);
        buildContent(box);

        return box;
    }

    protected abstract void buildContent(VBox container);
}