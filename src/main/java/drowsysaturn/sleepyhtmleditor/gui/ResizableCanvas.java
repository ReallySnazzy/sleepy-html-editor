package drowsysaturn.sleepyhtmleditor.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;

/**
 * This is the same as the regular JavaFX Canvas component, but with the added
 * benefit of being resizable. 
 * Example:
 * <code>
 * Region parent = ...;
 * ResizableCanvas canvas = new ResizableCanvas();
 * parent.getChildren().add(canvas);
 * canvas.bindDimsToParent(parent);
 * </code>
 */
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