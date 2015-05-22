package de.iisys.ocr.ocr;

import java.io.File;

/**
 * IOCR
 * de.iisys.ocr.ocr
 * Created by reza on 25.11.14.
 */
public interface IOCR {
    void setLanguage(String languageId);
    String runOCR(File imageFile) throws RuntimeException;
    String runOCR(String imagePath) throws RuntimeException;
}
