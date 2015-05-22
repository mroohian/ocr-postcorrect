package de.iisys.ocrcorpa;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Random;

/**
 * de.iisys.ocrcorpa
 * Created by reza on 19.11.14.
 */
public class TextRenderer {
    private final Font font;
    private Color color;
    private boolean noise;
    private int noiseLevel;
    private boolean forgroundNoise;
    private float forgroundNoiseLevel;

    public TextRenderer(String fontFace, int fontSize) {
        font = new Font(fontFace, Font.PLAIN, fontSize);
        color = Color.BLACK;
        noise = true;
        forgroundNoise = false;
        noiseLevel = 5;
        forgroundNoiseLevel = 5;
    }

    public TextRenderer() {
        this("Arial", 12);
    }

    public BufferedImage renderText(String text) {
        final int offsetX = 30;
        final int offsetY = 30;
        final int width = 1024;
        final int lineGap = 10;

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = img.createGraphics();

        AttributedString attributedString = new AttributedString(text);
        attributedString.addAttribute(TextAttribute.FONT, font);
        attributedString.addAttribute(TextAttribute.FOREGROUND, color);
        AttributedCharacterIterator characterIterator = attributedString.getIterator();
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);

        int lineY = offsetY;
        final int textWidth = width - offsetX - offsetX;
        while (measurer.getPosition() < characterIterator.getEndIndex()) {
            TextLayout textLayout = measurer.nextLayout(textWidth);
            lineY += textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading() + lineGap;
        }

        int height = lineY + offsetY;
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (noise) {
            float alpha = noiseLevel / 100.0f;
            // generate a low-res random image
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int val = new Random().nextInt(255);
                    g2d.setColor(new Color(val, val, val, (int) (alpha * 0xFF)));
                    g2d.fillRect(i, j, 1, 1);
                }
            }
        }


        int x = offsetX;
        int y = offsetY;
        measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);
        while (measurer.getPosition() < characterIterator.getEndIndex()) {
            TextLayout textLayout = measurer.nextLayout(textWidth);
            y += textLayout.getAscent();
            textLayout.draw(g2d, x, y);
            y += textLayout.getDescent() + textLayout.getLeading() + lineGap;
        }

        if (forgroundNoise) {
            float alpha = forgroundNoiseLevel / 100.0f;
            // generate a low-res random image
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int val = new Random().nextInt(255);
                    g2d.setColor(new Color(val, val, val, (int) (alpha * 0xFF)));
                    g2d.fillRect(i, j, 1, 1);
                }
            }
        }

        g2d.dispose();

        /*double skewX = 0.2;
        double skewY = 0.005;
        double x = (skewX < 0) ? -skewX*img.getHeight() : 0;
        AffineTransform at = AffineTransform.getTranslateInstance(x, 0);
        at.shear(skewX, skewY);
        BufferedImageOp op = new AffineTransformOp(at,
                new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC));
        //apply filter to image
        return op.filter(img, null);*/
        return img;
    }

    public void renderTextToPNGFile(File file, String text) {
        BufferedImage image = renderText(text);

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void renderTextToPNGFile(String fileName, String text) {
        renderTextToPNGFile(new File(fileName), text);
    }

    public void enableNoise() {
        noise = true;
    }

    public void disableNoise() {
        noise = false;
    }

    public void enableForgroundNoise() {
        forgroundNoise = true;
    }

    public void disbleForgroundNoise() {
        forgroundNoise = false;
    }

    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public int getNoiseLevel() {
        return noiseLevel;
    }

    public void setForgroundNoiseLevel(float forgroundNoiseLevel) {
        this.forgroundNoiseLevel = forgroundNoiseLevel;
    }

    public float getForgroundNoiseLevel() {
        return forgroundNoiseLevel;
    }
}
