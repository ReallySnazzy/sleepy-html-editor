package drowsysaturn.sleepyhtmleditor.html;

/**
 * Basically a counter except for names. Since the tree will be traversed in the same
 * order twice for style and body, the names will line up correctly. More information
 * on why the tree is traversed twice in HtmlExporter.
 */
public class ExporterNamer {
    private int index = 0;

    public String nextName() {
        return "e" + (++index);
    }
}