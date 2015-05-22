package de.iisys.ocr.app;

import de.iisys.ocr.config.Options;
import de.iisys.ocr.ocr.IOCR;
import de.iisys.ocr.ocr.OCRCorpusGenerator;
import de.iisys.ocr.ocr.TesseractOCR;
import de.iisys.ocr.pos.IPOSTagger;
import de.iisys.ocr.pos.StanfordEnglishPOSTagger;
import de.iisys.ocr.pos.StanfordGermanDewacPOSTagger;

/**
 * de.iisys.ocr.app
 * Created by reza on 27.11.14.
 */
public class OCRCorpusApp extends App {
    public OCRCorpusApp(Options options) {
        super(options);
    }

    @Override
    protected void run() {
        // Configs
        // - Tesseract OCR
        final String tesseractDataPath = getConfig("TesseractDataPath");
        final String tesseractLanguage = getConfig("TesseractLanguage");
        // - Archive reader
        final String archiveReaderPath = getConfig("ArchiveReaderPath");
        // - Standford POS tagger
        final String stanfordPOSTaggerModelPath = getConfig("StanfordPOSTaggerModelPath");
        // - OCR Courpus
        final String ocrCorpusPath = getConfig("OCRCorpusPath");


        // POS Tagger
        final IPOSTagger posTagger;
        if (tesseractLanguage.equals("deu")) {
            posTagger = new StanfordGermanDewacPOSTagger(stanfordPOSTaggerModelPath);
        } else if (tesseractLanguage.equals("eng")) {
            posTagger = new StanfordEnglishPOSTagger(stanfordPOSTaggerModelPath);
        } else {
            throw new RuntimeException("Invalid language id.");
        }

        // OCR
        final IOCR tesseractOCR = new TesseractOCR(tesseractDataPath);
        tesseractOCR.setLanguage(tesseractLanguage);

        // Corpus generator
        final OCRCorpusGenerator corpusGenerator = new OCRCorpusGenerator(tesseractOCR, posTagger);

        // Generate corpus
        final String archiveReaderBaseFolder = getConfig("ArchiveReaderBaseFolder");
        String archiveReaderFolderPath = archiveReaderPath + (archiveReaderPath.endsWith("/") ? "" : "/") + archiveReaderBaseFolder;
        String ocrCorpusFolderPath = ocrCorpusPath + (ocrCorpusPath.endsWith("/") ? "" : "/") + archiveReaderBaseFolder;

        corpusGenerator.generateCorpusFromArchive(archiveReaderFolderPath, ocrCorpusFolderPath);
    }
}
