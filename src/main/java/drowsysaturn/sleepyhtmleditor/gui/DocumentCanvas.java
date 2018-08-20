package drowsysaturn.sleepyhtmleditor.gui;

import java.awt.image.BufferedImage;

import drowsysaturn.sleepyhtmleditor.editor.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

/**
 * JavaFX canvas used to render DocumentLense.
 */
public class DocumentCanvas extends ResizableCanvas {
    private DocumentLense documentLense;
    private WritableImage writableImage;

    public DocumentCanvas() {
        documentLense = null;
        writableImage = null;
    }

    /**
     * Sets what lense to be used by this canvas.
     */
    public void setLense(DocumentLense lense) {
        this.documentLense = lense;
    }

    @Override
    /**
     * Draws the most recent lense. Doesn't render anything without a lense set.
     */
    public void draw() {
        GraphicsContext ctx = getGraphicsContext2D();
        ctx.setFill(Color.WHITE);
        ctx.fillRect(0, 0, getWidth(), getHeight());
        Image image = renderDocument();
        if (image != null) {
            ctx.drawImage(image, 0.0, 0.0);
        }
    }

    /**
     * Renders the document and converts it to the proper data format. 
     * @return The image or null if the DocumentLense is not ready.
     */
    private WritableImage renderDocument() {
       if (documentLense != null) {
            double width = getWidth();
            double height = getHeight();
            BufferedImage renderedDocument = documentLense.render((int)width, (int)height);
            if (renderedDocument != null) {
                writableImage = SwingFXUtils.toFXImage(renderedDocument, null);
            }
            return writableImage;
       } 
       return null;
    }
}