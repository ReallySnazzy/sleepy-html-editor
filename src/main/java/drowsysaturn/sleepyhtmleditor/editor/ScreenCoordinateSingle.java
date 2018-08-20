package drowsysaturn.sleepyhtmleditor.editor;

/**
 * Represents a single axis for a screen coordinate. 
 */
public class ScreenCoordinateSingle {
    
    private double percent;

    private int pixels;

    public ScreenCoordinateSingle(String string) throws CoordinateCodingException {
        ScreenCoordinateSingle single = CoordinateCoder.decode(string);
        this.percent = single.getPercent();
        this.pixels = single.getPixels();
    }

    public ScreenCoordinateSingle(double percent, int pixels) {
        this.percent = percent;
        this.pixels = pixels;
    }

    public double getPercent() {
        return percent;
    }

    public int getPixels() {
        return pixels;
    }
}