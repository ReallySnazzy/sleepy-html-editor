package drowsysaturn.sleepyhtmleditor.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateCoder {
    public static ScreenCoordinateSingle decode(String encodedString) throws CoordinateCodingException {
        Pattern pattern = Pattern.compile("\\s*(\\-?\\d*\\.?\\d*)\\%\\s*\\+\\s*(\\-?\\d+)px");
        Matcher matcher = pattern.matcher(encodedString);
        if (!matcher.find()) {
            throw new CoordinateCodingException();
        }
        String percentString = matcher.group(1);
        String pixelsString = matcher.group(2);
        double percent;
        int pixels;
        try {
            percent = Double.parseDouble(percentString)/100;
            pixels = Integer.parseInt(pixelsString);
        } catch (Exception ex) {
            throw new CoordinateCodingException(ex);
        }
        return new ScreenCoordinateSingle(percent, pixels);
    }

    public static String encode(ScreenCoordinateSingle coordinateSingle) {
        return coordinateSingle.getPercent()*100 + "%" + " + " + coordinateSingle.getPixels() + "px";
    }
}