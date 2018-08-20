package drowsysaturn.sleepyhtmleditor.editor;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import java.awt.Font;

/**
 * Web builder node that supports text.
 */
public class TextPanel extends StandardPanel {

    /**
     * Text for this panel.
     */
    private String text;

    /**
     * Horizontal alignment of text for this panel.
     */
    private TextAlign textAlign;

    private Color textColor;

    private String fontFamily;

    private int fontSize;

    private Font font;

    public TextPanel(ScreenCoordinate position, ScreenCoordinate size, String text) {
        super(position, size);
        setText(text);
        this.textAlign = TextAlign.LEFT;
        this.fontFamily = Font.SANS_SERIF;
        this.textColor = Color.BLACK;
        this.fontSize = 16;
        this.font = new Font(fontFamily, Font.PLAIN, fontSize);
    }

    public void setFontFamily(String fontFamily) {
        if (fontFamily == null) {
            throw new IllegalArgumentException("Font must not be null");
        }
        this.fontFamily = fontFamily;
        updateFont();
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontSize(int fontSize) {
        if (fontSize <= 0) {
            throw new IllegalArgumentException("Font size must not be negative");
        }
        this.fontSize = fontSize;
        updateFont();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setForegroundColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Foreground color must not be null");
        }
        this.textColor = color;
    }

    public Color getForegroundColor() {
        return textColor;
    }

    public void setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign;
    }

    public TextAlign getTextAlign() {
        return textAlign;
    }

    @Override
    public void render(Graphics graphics, int x, int y, int width, int height) {
        renderBackground(graphics, x, y, width, height);
        renderText(graphics, x, y, width, height);
        renderChildren(graphics, x, y, width, height);
    }

    @Override
    /**
     * Gets the element type.
     * @see DocumentElement
     */
    public String getElementType() {
        return TextPanel.class.getName();
    }

    private void updateFont() {
        font = new Font(fontFamily, Font.PLAIN, fontSize);
    }

    /**
     * Renders text based on the current settings into the given bounding box. 
     */
    private void renderText(Graphics graphics, int x, int y, int width, int height) {
        graphics.setFont(font);
        graphics.setColor(textColor);
        ArrayList<String> lines = splitLines(graphics, width);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textHeight = fontMetrics.getHeight();
        int textBaseHeight = fontMetrics.getAscent();
        if (textAlign == TextAlign.LEFT) {
            for (int i = 0; i < lines.size(); i++) {
                graphics.drawString(lines.get(i), x, y + i*textHeight + textBaseHeight);
            }
        }
        else if (textAlign == TextAlign.CENTER) {
            int middleX = x + width/2;
            for (int i = 0; i < lines.size(); i++) {
                int halfLineWidth = fontMetrics.stringWidth(lines.get(i))/2;
                graphics.drawString(lines.get(i), middleX - halfLineWidth, y + i*textHeight + textBaseHeight);
            }
        }
        else if (textAlign == TextAlign.RIGHT) {
            for (int i = 0; i < lines.size(); i++) {
                int lineWidth = fontMetrics.stringWidth(lines.get(i));
                graphics.drawString(lines.get(i), x + width - lineWidth, y + i*textHeight + textBaseHeight);
            }
        }
        else {
            throw new UnsupportedOperationException("Text align mode not supported");
        }
    }

    /**
     * Splits the text into lines based on the current font and the width of the panel.
     * @return List of lines.
     */
    private ArrayList<String> splitLines(Graphics graphics, int width) {
        String[] words = text.split("\\s");
        ArrayList<String> lines = new ArrayList<String>();
        FontMetrics fontMetrics = graphics.getFontMetrics();
        String currentLine = "";
        int wordIndex = 0;
        while (wordIndex < words.length) {
            while (wordIndex < words.length && fontMetrics.stringWidth(currentLine + words[wordIndex]) < width) {
                currentLine += words[wordIndex] + " ";
                wordIndex++;
            }
            lines.add(currentLine);
            currentLine = "";
        }
        return lines;
    }
}