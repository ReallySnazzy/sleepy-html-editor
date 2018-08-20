package drowsysaturn.sleepyhtmleditor.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;

public abstract class ResizableCanvas extends Canvas {
    public ResizableCanvas() {
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    public abstract void draw();

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double width) {
        return getWidth();
    }

    @Override
    public double prefHeight(double height) {
        return getHeight();
    }

    public void bindDimsToParent(Region parent) {
        widthProperty().bind(parent.widthProperty());
        heightProperty().bind(parent.heightProperty());
    }
}