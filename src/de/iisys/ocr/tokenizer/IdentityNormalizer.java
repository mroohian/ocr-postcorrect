package de.iisys.ocr.tokenizer;

/**
 * IdentityNormalizer
 * de.iisys.ocr.tokenizer
 * Created by reza on 22.08.14.
 */
public class IdentityNormalizer implements INormalizer {
    @Override
    public String removeBadChars(String input) {
        return input;
    }

    @Override
    public String normalize(String input) {
        return input;
    }
}
