package de.iisys.ocr.ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * TesseractOCR
 * de.iisys.ocr.test.core.ocr
 * Created by reza on 25.11.14.
 */
public class TesseractOCR implements IOCR {
    private String dataPath;
    private String languageId;

    public TesseractOCR(String dataPath) {
        this.dataPath = dataPath;
        // Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping

    }

    public void setLanguage(String languageId) {
        this.languageId = languageId;
    }

    public synchronized String runOCR(File imageFile) throws RuntimeException {
        Tesseract instance = Tesseract.getInstance();  // JNA Interface Mapping
        instance.setDatapath(dataPath);
        instance.setLanguage(languageId);

        try {
            return instance.doOCR(imageFile);
        } catch (TesseractException ex) {
            throw new RuntimeException(ex);
        } catch (OutOfMemoryError ex) {
            throw new RuntimeException(ex);
        }
    }

    public String runOCR(String imagePath) throws RuntimeException {
        return runOCR(new File(imagePath));
    }

}
