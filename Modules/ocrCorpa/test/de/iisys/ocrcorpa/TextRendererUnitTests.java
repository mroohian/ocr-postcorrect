package de.iisys.ocrcorpa;

import org.junit.Test;

/**
 * de.iisys.ocrcorpa
 * Created by reza on 19.11.14.
 */
public class TextRendererUnitTests {
    @Test
    public void testRenderTest() {
        final String text = "This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.This is a test of text renderer. This is a test of text renderer. This is a test of text renderer. This is a test of text renderer.";
        TextRenderer textRenderer = new TextRenderer("DejaVu Serif", 24);

        textRenderer.renderTextToPNGFile("test.png", text);
    }
}
