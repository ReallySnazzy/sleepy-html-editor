package drowsysaturn.sleepyhtmleditor.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This is an empty box element. 
 * Can be filled with a color or a background image. Background images have various styles.
 * To change the background style use setBackgroundStyle.
 * 
 * Example usage:
 * <code>
 * ScreenCoordinate position = new ScreenCoordinate(-400, 0.5, -200, 0.5);
 * ScreenCoordinate size = new ScreenCoordinate(800, 0, 400, 0);
 * StandardPanel panel = new StandardPanel(position, size);
 * panel.setBackgroundColor(java.awt.Color.RED);
 * panel.setImage(new File("money.jpg"));
 * panel.setBackgroundStyle(BackgroundStyle.STRETCH);
 * documentLense.getRoot().addChild(panel);
 * </code>
 */
public class StandardPanel extends DocumentElement {
    /**
     * File to use for generating this panel.
     */
    private File imageFile;

    /**
     * Color to use for the background
     */
    private Color color;

    /**
     * Background style to use for this panel.
     */
    private BackgroundStyle style;
    
    /**
     * Panel pre-rendered.
     */
    private BufferedImage buffer;

    /**
     * Last width and height of panel since prerender.
     */
    private int lastWidth, lastHeight;

    /**
     * Image has changed since last prerender.
     */
    private boolean imageChanged;

    /**
     * Determines if the File object or Image is saved in memory.
     */
    private boolean imageIsFile;

    /**
     * Image saved in memory or null.
     */
    private BufferedImage loadedImage;

    public StandardPanel(ScreenCoordinate position, ScreenCoordinate size) {
        super(position, size);
        imageFile = null;
        lastWidth = 0;
        lastHeight = 0;
        color = Color.GRAY;
        style = BackgroundStyle.ORIGINAL;
        imageChanged = false;
        imageIsFile = true;
        loadedImage = null;
    }

    /**
     * Sets the background style to one of the BackgroundStyle options.
     * @throws IllegalArgumentException If the specified style is null.
     */
    public void setBackgroundStyle(BackgroundStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("Background style may not be null");
        }
        this.style = style;
        this.imageChanged = true;
    }

    /**
     * Gets the previously set background style for this object. Defaults to BackgroundStyle.ORIGINAL.
     */
    public BackgroundStyle getBackgroundStyle() {
        return style;
    }

    /**
     * Sets the background color for the standard panel.
     * @throws IllegalArgumentException If null is specified as the background color.
     */
    public void setBackgroundColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("StandardPanel must have a non-null color.");
        }
        this.color = color;
        this.imageChanged = true;
    }

    /**
     * Gets the previously set background color of the standard panel. Defaults to Color.GRAY.
     */
    public Color getBackgroundColor() {
        return color;
    }

    /**
     * Sets the image to use for background.
     */
    public void setImage(File imageFile) {
        this.imageFile = imageFile;
        imageChanged = true;
        imageIsFile = true;
    }

    /**
     * Gets the image file if one was added and this panel's image mode is to load from a file.
     * @see setImage(File)
     * @see setImage(BufferedImage)
     */
    public File getImageFile() {
        if (!imageIsFile) {
            return null;
        }
        return imageFile;
    }

    /**
     * Sets the image to use for background.
     */
    public void setImage(BufferedImage image) {
        this.loadedImage = image;
        imageIsFile = false;
    }

    @Override
    /**
     * Gets the element type. 
     * @see {#DocumentElement.getElementType()}
     */
    public String getElementType() {
        return StandardPanel.class.getName();
    }

    @Override
    public void render(Graphics graphics, int x, int y, int width, int height) {
        renderBackground(graphics, x, y, width, height);
        renderChildren(graphics, x, y, width, height);
    }

    protected void renderBackground(Graphics graphics, int x, int y, int width, int height) {
        if (bufferExpired(width, height)) {
            prerender(width, height);
        }
        graphics.drawImage(buffer, x, y, width, height, null);
        if (selectionBoxOn) {
            graphics.setColor(Color.RED);
            graphics.drawRect(x, y, width - 1, height - 1);
        }
    }

    private boolean bufferExpired(int width, int height) {
        if (lastWidth != width || lastHeight != height || imageChanged) {
            return true;
        }
        return false;
    }

    private void prerender(int width, int height) {
        imageChanged = false;
        lastWidth = width;
        lastHeight = height;
        BufferedImage buf = generateBuffer(width, height);
        Graphics graphics = buf.getGraphics();
        renderBackgroundColor(graphics, width, height);
        renderBackgroundPattern(graphics, width, height);
        this.buffer = buf;
    }

    private void renderBackgroundColor(Graphics graphics, int width, int height) {
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
    }

    private void renderBackgroundPattern(Graphics graphics, int width, int height) {
        if (style == BackgroundStyle.SOLID_COLOR) {
            return;
        }
        BufferedImage image = imageIsFile ? loadImageOrNull() : loadedImage;
        if (image != null) {
            int imageWidth = image.getWidth(), imageHeight = image.getHeight();
            if (style == BackgroundStyle.STRETCH) {
                graphics.drawImage(image, 0, 0, width, height, null);
            }
            else if (style == BackgroundStyle.COVER) {
                renderCoverPattern(graphics, image, width, height, imageWidth, imageHeight);
            }
            else if (style == BackgroundStyle.REPEAT) {
                renderRepeatPattern(graphics, image, width, height, imageWidth, imageHeight);
            }
            else if (style == BackgroundStyle.ORIGINAL) {
                graphics.drawImage(image, 0, 0, null);
            }
        }
    }

    private void renderCoverPattern(Graphics graphics, BufferedImage image, int width, int height, int imageWidth, int imageHeight) {
        float ratio = (float)imageWidth/(float)imageHeight;
        int coverWidth = (int)Math.round(ratio*height);
        int coverHeight = height;
        if (coverWidth > width) {
            float fixMultiplier = (float)width/(float)coverWidth;
            coverWidth *= fixMultiplier;
            coverHeight *= fixMultiplier;
        }
        int middleX = -coverWidth/2 + width/2;
        int middleY = -coverHeight/2 + width/2;
        graphics.drawImage(image, middleX, middleY, coverWidth, coverHeight, null);
    }

    private void renderRepeatPattern(Graphics graphics, BufferedImage image, int width, int height, int imageWidth, int imageHeight) {
        for (int x = 0; x < width; x += imageWidth) {
            for (int y = 0; y < height; y += imageHeight) {
                graphics.drawImage(image, x, y, null);
            }
        }
    }

    /**
     * Loads the image from file or returns null.
     */
    private BufferedImage loadImageOrNull() {
        if (imageFile == null) {
            return null;
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageFile);
        }
        catch (IOException ex) { }
        return image;
    }

    private BufferedImage generateBuffer(int width, int height) {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return buf;
    }
}