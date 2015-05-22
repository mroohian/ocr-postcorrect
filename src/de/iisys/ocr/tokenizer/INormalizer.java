package de.iisys.ocr.tokenizer;

/**
 * INormalizer
 * de.iisys.ocr.tokenizer
 * Created by reza on 22.08.14.
 */
public interface INormalizer {
    public String removeBadChars(String input);
    public String normalize(String input);
}
