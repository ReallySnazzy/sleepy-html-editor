package drowsysaturn.sleepyhtmleditor.editor;

import java.awt.image.BufferedImage;

/**
 * The wrapper around the document tree, allows easier access to tree functions. 
 * Maintains the root node. Helps abstract selection, deletion, and rendering.
 */
public class DocumentLense {
    private DocumentElement root;

    public DocumentLense(DocumentElement root) {
        if (root == null) {
            throw new IllegalArgumentException("Document root must not be null.");
        }
        this.root = root;
    }

    /**
     * The bottom element in the document.
     */
    public DocumentElement getRoot() {
        return root;
    }

    /**
     * Creates a display image of the given dimensions.
     * @return The image or null if the document is not ready.
     * @throws RuntimeException If the root node of this document is null.
     */
    public BufferedImage render(int width, int height) {
        if (root == null) {
            throw new RuntimeException("Document root must not be null.");
        }
        if (width == 0 || height == 0) {
            // Rendering before layout has updated. Skipping.
            return null;
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ScreenCoordinate absolutePosition = root.getPosition().toAbsoluteCoordinate(width, height);
        ScreenCoordinate absoluteSize = root.getSize().toAbsoluteCoordinate(width, height);
        root.render(image.getGraphics(), absolutePosition.getPixelsX(), absolutePosition.getPixelsY(), 
                absoluteSize.getPixelsX(), absoluteSize.getPixelsY());
        return image;
    }

    /**
     * Removes the given element from the DocumentElement tree.
     */
    public boolean removeElement(DocumentElement element) {
        if (root == null) {
            throw new RuntimeException("Root node must exist to remove an element");
        }
        return root.removeChild(element);
    }

    /**
     * Selects the top element from the cursor X and cursor Y.
     */
    public DocumentElement select(int width, int height, int cursorX, int cursorY) {
        ScreenCoordinate absolutePosition = root.getPosition().toAbsoluteCoordinate(width, height);
        ScreenCoordinate absoluteSize = root.getSize().toAbsoluteCoordinate(width, height);
        return root.select(absolutePosition.getPixelsX(), absolutePosition.getPixelsY(), 
            absoluteSize.getPixelsX(), absoluteSize.getPixelsY(), 
            cursorX, cursorY);
    }
}