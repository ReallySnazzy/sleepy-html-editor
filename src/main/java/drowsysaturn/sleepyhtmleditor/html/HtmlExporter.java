package drowsysaturn.sleepyhtmleditor.html;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import drowsysaturn.sleepyhtmleditor.editor.DocumentElement;
import drowsysaturn.sleepyhtmleditor.editor.DocumentLense;
import drowsysaturn.sleepyhtmleditor.editor.ScreenCoordinate;
import drowsysaturn.sleepyhtmleditor.editor.StandardPanel;
import drowsysaturn.sleepyhtmleditor.editor.TextPanel;

/**
 * Exports a document tree into html.
 * 
 * The document tree is traversed twice to while exporting because the file is not kept in memory.
 * The tree is written out as the tree is traversed to lower memory usage.
 */
public class HtmlExporter {
    private DocumentLense lense;

    private String documentTitle;

    public HtmlExporter(DocumentLense lense, String documentTitle) {
        this.lense = lense;
        this.documentTitle = documentTitle;
    }

    /**
     * Writes the HTML document to the file. Calls necessary functions to
     * write individual parts of the document.
     */
    public void write(File file) throws IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(file));
        writer.println("<!DOCTYPE html PUBLIC>");
        writer.println("<html>");
        writer.println("<head>");
        writer.print("\t<title>");
        writeTitle(writer);
        writer.println("</title>");
        writer.println("<style>");
        writeStyle(writer);
        writer.println("</style>");
        writer.println("</head>");
        writer.println("<body>");
        writeBody(writer);
        writer.println("</body>");
        writer.println("</html>");
        writer.flush();
        writer.close();
    }

    public void writeTitle(PrintWriter writer) throws IOException {
        writer.print(documentTitle);
    }

    public void writeStyle(PrintWriter writer) throws IOException {
        writeStyleTree(writer, new ExporterNamer(), lense.getRoot());
    }

    public void writeBody(PrintWriter writer) throws IOException {
        writeBodyTree(writer, new ExporterNamer(), lense.getRoot());
    }

    private void writeStyleTree(PrintWriter writer, ExporterNamer namer, DocumentElement element) throws IOException {
        String name = namer.nextName();
        writer.println("." + name + " {");
        if (element instanceof TextPanel) {
            TextPanel elementTextPanel = (TextPanel)element;
            writer.println("font:" + elementTextPanel.getFontSize() + "px " + elementTextPanel.getFontFamily() + ";");
            writer.println("color: " + colorRgba(elementTextPanel.getForegroundColor()) + ";");
        }
        if (element instanceof StandardPanel) {
            StandardPanel elementStandardPanel = (StandardPanel)element;
            writer.println("background-color: " + colorRgba(elementStandardPanel.getBackgroundColor()) + ";");
            writer.println("display: block;");
        }
        ScreenCoordinate position = element.getPosition();
        ScreenCoordinate size = element.getSize();
        writer.println("position: absolute;");
        writer.println("left: calc(" + position.getPercentX()*100 + "vw + " + position.getPixelsX() + "px);");
        writer.println("top: calc(" + position.getPercentY()*100 + "vh + " + position.getPixelsY() + "px);");
        writer.println("width: calc(" + size.getPercentX()*100 + "vw + " + size.getPixelsX() + "px);");
        writer.println("height: calc(" + size.getPercentY()*100 + "vh + " + size.getPixelsY() + "px);");
        writer.println("}");
        for (DocumentElement child : element.getChildren()) {
            writeStyleTree(writer, namer, child);
        }
    }

    private String colorRgba(Color color) {
        return "rgba(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", " + ((double)color.getAlpha()/255) + ")";
    }

    private void writeBodyTree(PrintWriter writer, ExporterNamer namer, DocumentElement element) throws IOException {
        String name = namer.nextName();
        writer.print("<div class=\"" + name + "\">");
        if (element instanceof TextPanel) {
            writer.print(((TextPanel)element).getText());
        }
        for (DocumentElement child : element.getChildren()) {
            writeBodyTree(writer, namer, child);
        }
        writer.println("</div>");
    }
}