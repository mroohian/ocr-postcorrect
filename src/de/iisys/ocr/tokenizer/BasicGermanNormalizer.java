package de.iisys.ocr.tokenizer;

/**
 * BasicGermanNormalizer
 * de.iisys.ocr.tokenizer
 * Created by reza on 22.08.14.
 */
public class BasicGermanNormalizer implements INormalizer {
    @Override
    public String removeBadChars(String input) {
        // Replaces wrong umlaut and ß characters with `_`
        return input.replace((char)0xFFFD, '_');
    }

    @Override
    public String normalize(String input) {
        return input.replaceAll("[0-9]", "0");
    }
}
