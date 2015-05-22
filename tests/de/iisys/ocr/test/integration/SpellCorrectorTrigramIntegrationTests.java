package de.iisys.ocr.test.integration;

import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.ocr.OCRCorpusGenerator;
import de.iisys.ocr.ocr.OCRCorpusFileGeneratorRunnable;
import de.iisys.ocr.ocr.TesseractOCR;
import de.iisys.ocr.pos.*;
import de.iisys.ocr.spellcorrector.trigram.SpellCorrectorTrigramTransducer;
import de.iisys.ocr.spellcorrector.trigram.lattice.SpellCorrectorTrigramLattice;
import de.iisys.ocr.test.core.archive.ArchiveReaderUnit;
import de.iisys.ocr.test.unit.spellcorrector.trigram.SpellCorrectorTransducerTrigramUnitTests;
import de.iisys.ocr.textalignment.LevenshteinTextAlignerResult;
import de.iisys.ocr.textalignment.LevenshteinTextAlignerResultSet;
import de.iisys.ocr.tokenizer.ITokenizer;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNodeList;
import de.iisys.ocr.types.Token;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * SpellCorrectorTrigramIntegrationTests
 * de.iisys.ocr.test.integration
 * Created by reza on 26.11.14.
 */
public class SpellCorrectorTrigramIntegrationTests extends SpellCorrectorTransducerTrigramUnitTests {
    // TODO: fix to work with the new multithreaded version
    /*
    @Test
    public void testArchiveReaderOCRTrigramLattice() {
        final TesseractOCR tesseract = new TesseractOCR(mTesseractDataPath);
        tesseract.setLanguage("deu");
        final IPOSTagger posTagger = new StanfordGermanDewacPOSTagger(mStanfordPOSTaggerModelPath);
        final OCRCorpusGenerator corpusGenerator = new OCRCorpusGenerator(tesseract, posTagger);

        ArchiveReader archiveReader = new ArchiveReader(new ArchiveReader.IArchiveReaderCallback() {
            @Override
            public void processFileContent(ArchiveReader archiveReader1, File file, String content) {
                OCRCorpusFileGeneratorRunnable runnable = new OCRCorpusFileGeneratorRunnable("", content, );
                corpusGenerator.generateTextImage(content);
                List<ISentence> sentences = posTagger.extractSentences(content);

                StringBuilder alignerContent = new StringBuilder();
                for (ISentence sentence: sentences) {
                    for (HasWord token : (StanfordPOSTagger.StanfordSentence)sentence) {
                        alignerContent.append(token.word() + " ");
                    }
                }

                LevenshteinTextAlignerResultSet result = corpusGenerator.ocrAndAlign(alignerContent.toString());

                int n = 0;
                for (ISentence sentence: sentences) {
                    posTagger.tagSentence(sentence);

                    for (CoreLabel token : (StanfordPOSTagger.StanfordSentence)sentence) {
                        LevenshteinTextAlignerResult<String, String> nextResult = result.get(n++);
                        // TODO: remove for release {
                        /* assert nextResult.output.equals(token.word());
                        System.out.println( token.word() + " / " + nextResult.input + " / " + token.tag());* /
                        // TODO: }

                        token.set(OCRAnnotation.class, nextResult.input);
                    }
                }

                final double L = 2.5;

                ITokenizer inputTokenizer = result.getInputTokenizer();
                Token[] inputTokens = Token.createTokenArray(inputTokenizer);

                ITokenizer outputTokenizer = result.getOutputTokenizer();
                Token[] outputTokens = Token.createTokenArray(outputTokenizer);

                // Features
                Map<IFeature, Double> mFeaturesMap = featureMap(L);

                // Our model
                final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

                // Compute unlabeled lattice
                SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens, outputTokens);

                for (ITrigramLatticeNodeList latticeNodes : lattice) {
                    System.out.print(latticeNodes.size() + " ");
                    for (ILatticeNode latticeNode : latticeNodes) {
                        System.out.print(latticeNode.getNode().getWord() + " ");
                    }
                    System.out.println();
                }

                archiveReader1.cancelOperation();
            }

            @Override
            public void handleFileError(ArchiveReader archiveReader, File file, Exception ex) {
            }

            @Override
            public void processDirChanged(File dir) {
            }

        });

        ArchiveReaderUnit archiveReaderUnit = new ArchiveReaderUnit(mArchivePath + "/de/health");
        archiveReader.readDir(new File(archiveReaderUnit.getPath()));
    }
    */
}
