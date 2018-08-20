package drowsysaturn.sleepyhtmleditor.editor;

/**
 * Used to convert BackgroundStyle elements to Strings and vice versa.
 */
public class BackgroundStyleHelper {
    /**
     * Converts the BackgroundStyle object to a string representation of it.
     */
    public static String toString(BackgroundStyle style) {
        switch (style) {
            case SOLID_COLOR:
                return "Solid color";
            case STRETCH:
                return "Stretch";
            case COVER:
                return "COVER";
            case ORIGINAL:
                return "Original";
            case REPEAT:
                return "Repeat";
            default:
                return "Original";
        }
    }

    /**
     * Converts the string representation of the background style to a BackgroundStyle.
     */
    public static BackgroundStyle parseString(String style) {
        switch (style) {
            case "Solid color":
                return BackgroundStyle.SOLID_COLOR;
            case "Stretch":
                return BackgroundStyle.STRETCH;
            case "Cover":
                return BackgroundStyle.COVER;
            case "Original":
                return BackgroundStyle.ORIGINAL;
            case "Repeat":
                return BackgroundStyle.REPEAT;
            default:
                return BackgroundStyle.ORIGINAL;
        }
    }
}