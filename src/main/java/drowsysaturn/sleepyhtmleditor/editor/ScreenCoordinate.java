package drowsysaturn.sleepyhtmleditor.editor;


/**
 * Representation of a portion of a parent with a given percentage of parent and offset of the parent in pixels.
 * Either percentage or pixels may be negative to give increased usage of ScreenCoordinate.
 */
public class ScreenCoordinate {
    private int pixelsX;

    private double percentX;

    private int pixelsY;

    private double percentY;

    public ScreenCoordinate(int pixelsX, double percentageX, int pixelsY, double percentageY) {
        this.pixelsX = pixelsX;
        this.percentX = percentageX;
        this.pixelsY = pixelsY;
        this.percentY = percentageY;
    }

    public ScreenCoordinate(ScreenCoordinateSingle x, ScreenCoordinateSingle y) {
        this.pixelsX = x.getPixels();
        this.percentX = x.getPercent();
        this.pixelsY = y.getPixels();
        this.percentY = y.getPercent();
    }

    public int getPixelsX() {
        return pixelsX;
    }

    public double getPercentX() {
        return percentX;
    }

    public int getPixelsY() {
        return pixelsY;
    }

    public double getPercentY() {
        return percentY;
    }

    /**
     * Removes the percentage element from a screen coordinate and converts it into pixels offset.
     */
    public ScreenCoordinate toAbsoluteCoordinate(int parentWidth, int parentHeight) {
        int x = (int)Math.round(getPercentX()*parentWidth) + getPixelsX();
        int y = (int)Math.round(getPercentY()*parentHeight) + getPixelsY();
        return new ScreenCoordinate(x, 0, y, 0);
    }
}