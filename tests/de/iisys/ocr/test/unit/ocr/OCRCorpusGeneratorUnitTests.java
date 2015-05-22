package de.iisys.ocr.test.unit.ocr;

import de.iisys.ocr.ocr.OCRCorpusGenerator;
import de.iisys.ocr.ocr.TesseractOCR;
import de.iisys.ocr.pos.IPOSTagger;
import de.iisys.ocr.pos.StanfordGermanDewacPOSTagger;
import org.junit.Test;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.archive.ArchiveReaderUnit;

/**
 * OCRCorpusGeneratorUnitTests
 * de.iisys.ocr.test.other
 * Created by reza on 19.11.14.
 */
public class OCRCorpusGeneratorUnitTests extends BaseTest {
    @Test
    public void OCRCorpusGeneratorUnitTests() {
        final IPOSTagger posTagger = new StanfordGermanDewacPOSTagger(mStanfordPOSTaggerModelPath);
        final TesseractOCR tesseract = new TesseractOCR(mTesseractDataPath);
        tesseract.setLanguage("deu");
        final OCRCorpusGenerator corpusGenerator = new OCRCorpusGenerator(tesseract, posTagger);

        ArchiveReaderUnit archiveReaderUnit = new ArchiveReaderUnit(mArchivePath + "/de/");
        //ArchiveReaderUnit archiveReaderUnit = new ArchiveReaderUnit(mArchivePath + "/de/health/GesundheitRatgeberFNWeb/y2014/m1/d18/");
        //ArchiveReaderUnit archiveReaderUnit = new ArchiveReaderUnit(mArchivePath + "/de/health/KSTAGesundheit/y2014/m6/d20/"); // empty
        corpusGenerator.generateCorpusFromArchive(archiveReaderUnit.getPath(), "/home/reza/Desktop/ocrCorpus/test/");
    }
}
