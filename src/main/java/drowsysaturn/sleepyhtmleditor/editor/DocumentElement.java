package drowsysaturn.sleepyhtmleditor.editor;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


/**
 * Abstract representation of anything added to the website drawing.
 */
public abstract class DocumentElement {
    /**
     * List of children.
     */
    protected LinkedList<DocumentElement> children;

    protected boolean selectionBoxOn = false;

    /**
     * Position of this element relative to its parent.
     */
    private ScreenCoordinate position;

    /**
     * Size of this element relative to its parent.
     */
    private ScreenCoordinate size;

    public DocumentElement(ScreenCoordinate position, ScreenCoordinate size) {
        this.position = position;
        this.size = size;
        this.children = new LinkedList<DocumentElement>();
    }

    /**
     * Gets the position of the current node relative to its parent.
     */
    public ScreenCoordinate getPosition() {
        return position;
    }

    public LinkedList<DocumentElement> getChildren() {
        return children;
    }

    /**
     * Gets the size of the current node relative to its parent.
     */
    public ScreenCoordinate getSize() {
        return size;
    }

    public void showSelectionBox(boolean selectionBoxOn) {
        this.selectionBoxOn = selectionBoxOn;
    }

    /**
     * Sets the position of the current node relative to its parent.
     */
    public void setPosition(ScreenCoordinate position) {
        this.position = position;
    }

    /**
     * Sets the size of the current node relative to its parent.
     */
    public void setSize(ScreenCoordinate size) {
        this.size = size;
    }

    public abstract void render(Graphics graphics, int x, int y, int width, int height);

    /**
     * Renders all the children of this element in the given dimensions.
     */
    public void renderChildren(Graphics graphics, int parentX, int parentY, int parentWidth, int parentHeight) {
        for (DocumentElement child : children) {
            ScreenCoordinate absolutePosition = child.getPosition().toAbsoluteCoordinate(parentWidth, parentHeight);
            ScreenCoordinate absoluteSize = child.getSize().toAbsoluteCoordinate(parentWidth, parentHeight);
            int x = parentX + absolutePosition.getPixelsX();
            int y = parentY + absolutePosition.getPixelsY();
            int width = absoluteSize.getPixelsX();
            int height = absoluteSize.getPixelsY();
            child.render(graphics, x, y, width, height);
        }
    }

    public DocumentElement select(int x, int y, int width, int height, int cursorX, int cursorY) {
        for (Iterator<DocumentElement> i = children.descendingIterator(); i.hasNext(); ) {
            DocumentElement child = i.next();
            ScreenCoordinate childAbsPos = child.getPosition().toAbsoluteCoordinate(width, height);
            int childX = x + childAbsPos.getPixelsX();
            int childY = y + childAbsPos.getPixelsY();
            ScreenCoordinate childAbsSize = child.getSize().toAbsoluteCoordinate(width, height);
            int childWidth = childAbsSize.getPixelsX();
            int childHeight = childAbsSize.getPixelsY();
            DocumentElement selected = child.select(childX, childY, childWidth, childHeight, cursorX, cursorY);
            if (selected != null) {
                return selected;
            }
        }
        if (cursorX < width + x && cursorX >= x) {
            if (cursorY < height + y && cursorY >= y) {
                return this;
            }
        }
        return null;
    }

    /**
     * Should return the short class name. Example: DocumentElement for this class.
     * <code>
     * @Override
     * public String getElementType() {
     *  return DocumentElement.class.getName();
     * }
     * </code>
     */
    public abstract String getElementType();

    /**
     * Adds the element to the current node's immediate children. Only adds a child if it doesn't
     * already exist in this node's children.
     * @return True if the child was added.
     */
    public boolean addChild(DocumentElement element) {
        boolean shouldAddElement = !this.children.contains(element);
        if (shouldAddElement) {
            this.children.add(element);
        }
        return shouldAddElement;
    }

    /**
     * Removes the specified element from this nodes descendants. This is the same as calling
     * <code>
     * removeChild(element, true);
     * </code>
     */
    public boolean removeChild(DocumentElement element) {
        return removeChild(element, true);
    }

    /**
     * Removes the specified element from either the immediate children or descendants.
     * @param recursive Determines if descendants should be searched too.
     */
    public boolean removeChild(DocumentElement element, boolean recursive) {
        boolean removed = children.remove(element);
        if (!removed && recursive) {
            for (DocumentElement child : children) {
                removed = child.removeChild(element, true);
                if (removed) {
                    break;
                }
            }
        }
        return removed;
    }
}