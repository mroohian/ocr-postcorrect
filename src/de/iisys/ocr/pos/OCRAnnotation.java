package de.iisys.ocr.pos;

import edu.stanford.nlp.ling.CoreAnnotation;

/**
 * OCRAnnotation
 * de.iisys.ocr.pos
 * Created by reza on 26.11.14.
 */
public class OCRAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
        return String.class;
    }
}
