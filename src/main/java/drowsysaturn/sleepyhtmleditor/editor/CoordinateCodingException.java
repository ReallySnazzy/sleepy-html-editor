package drowsysaturn.sleepyhtmleditor.editor;

public class CoordinateCodingException extends Exception {
    public CoordinateCodingException() {
        super("Invalid coordinate encoding");
    }

    public CoordinateCodingException(Exception ex) {
        super(ex);
    }
}