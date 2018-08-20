package drowsysaturn.sleepyhtmleditor.gui;

import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 * Various JavaFX utilities.
 */
public class FxHelper {
    /**
     * Makes sure the runnable is executed on the FX thread.
     */
    public static void executeFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public static Color fromAwtColorToFx(java.awt.Color c) {
        return new Color((double)c.getRed()/255, (double)c.getGreen()/255, (double)c.getBlue()/255, (double)c.getAlpha()/255);
    }

    public static java.awt.Color fromFxColorToAwt(Color c) {
        return new java.awt.Color((int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255), (int)(c.getOpacity()*255));
    }
}