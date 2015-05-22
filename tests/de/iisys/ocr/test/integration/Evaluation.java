package de.iisys.ocr.test.integration;

import de.iisys.levdistcorpa.alphabet.WordAlphabet;
import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.levdistcorpa.types.StartNode;
import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.ocr.OCRCorpusSentence;
import de.iisys.ocr.spellcorrector.trigram.SpellCorrectorTrigramTransducer;
import de.iisys.ocr.spellcorrector.trigram.features.SpellCorrectorDistanceTrigramFeature;
import de.iisys.ocr.spellcorrector.trigram.features.SpellCorrectorMixedTrigramFeature;
import de.iisys.ocr.spellcorrector.trigram.lattice.SpellCorrectorTrigramLattice;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.WordPOSCorpaUnit;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluation
 * Created by reza on 04.02.15.
 */
public class Evaluation extends BaseTest {
    private static WordPOSCorpaUnit wordPOSCorpaUnit;
    //private static DeReWoCorpaUnit deReWoCorpaUnit;

    @BeforeClass
    public static void setup() throws IOException, ClassNotFoundException {
        wordPOSCorpaUnit = new WordPOSCorpaUnit(mPOSAlphabetFile, mWordAlphabetFile, mWordPOSMappingFile);
        //deReWoCorpaUnit = new DeReWoCorpaUnit(mCorpusFile);
    }

    @Test
    public void testEvaluateIsolated() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = wordPOSCorpaUnit.getCorpa();
        //final TokenCorpa corpa = deReWoCorpaUnit.getCorpa();

        // Our model
        final ITrigramTransducer transducer = createModelIsolated(corpa);

        runTest(transducer, corpa);
    }

    @Test
    public void testEvaluateContext() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = wordPOSCorpaUnit.getCorpa();
        //final TokenCorpa corpa = deReWoCorpaUnit.getCorpa();

        // Our model
        final ITrigramTransducer transducer = createModelContext(corpa);

        runTest(transducer, corpa);
    }


    public void runTest(final ITrigramTransducer transducer, final TokenCorpa corpa) throws IOException, ClassNotFoundException {
        final WordAlphabet originalAlphabet = new WordAlphabet(mWordAlphabetFile);

        final String ocrCorpusDirectory = "/home/reza/Desktop/ocrCorpus/en/health/HealthGoogleNews/y2014";

        final long[] statistics = {
                0, // 0- Total
                0, // 1- xxx
                0, // 2- xxy
                0, // 3- xyx real-word
                0, // 4- xyz real-word
                0, // 5- xyy real-word
                0, // 6- xyx non-word
                0, // 7- xyx non-word
                0  // 8- xyx non-word
        };

        ArchiveReader archiveReader = new ArchiveReader(new ArchiveReader.IArchiveReaderCallback() {
            @Override
            public void processFileContent(ArchiveReader archiveReader1, File file) {
                System.out.println(file.getAbsolutePath());

                try {
                    OCRCorpusSentence[] ocrCorpusSentences = ArchiveReader.readOCRCorpusFile(file);

                    for (OCRCorpusSentence sentence : ocrCorpusSentences) {
                        String[] groundTruth = sentence.getWords();
                        boolean[] isCorrect = sentence.getCorrect();

                        final String[] ocrWords = sentence.getOcrWords();
                        final String[] posTags = sentence.getPosTags();
                        final Short[] posTagIDs = new Short[posTags.length + 4];
                        posTagIDs[0] = posTagIDs[1] = StartNode.SHORT_INDEX;
                        posTagIDs[posTagIDs.length-1] = posTagIDs[posTagIDs.length-2] = EndNode.SHORT_INDEX;
                        for (int i = 2; i < posTags.length + 2; i++) posTagIDs[i] = corpa.getPOSID(posTags[i-2]);
                        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, ocrWords, posTagIDs);

                        lattice.computeViterbi();

                        INode[] labels = lattice.getOptimumLabelSequence();

                        for (int i = 0; i < labels.length; i++) {
                            final String ground = groundTruth[i];
                            final String ocr = ocrWords[i];
                            final String correction = corpa.getWordById(labels[i].getWordId());

                            final boolean ocrCorrect = isCorrect[i];
                            final boolean postProcessorCorrect = correction.equals(ground);

                            statistics[0]++; // Total

                            if (ocrCorrect) { // xx
                                if (postProcessorCorrect) { // xxx
                                    statistics[1]++;
                                } else { // xxy
                                    statistics[2]++;
                                }
                            } else { // xy
                                boolean isReal;

                                try {
                                    originalAlphabet.lookup(ocr);
                                    isReal = true;
                                } catch (Exception ex) {
                                    isReal = false;
                                }

                                if (isReal) {
                                    if (postProcessorCorrect) { // ER-xyx
                                        statistics[3]++;
                                    } else if (ocr.equals(correction)) { // ER-xyy
                                        statistics[4]++;
                                    } else { // ER-xyz
                                        statistics[5]++;
                                    }
                                } else {
                                    if (postProcessorCorrect) { // EN-xyx
                                        statistics[6]++;
                                    } else if (ocr.equals(correction)) { // EN-xyy
                                        statistics[7]++;
                                    } else { // EN-xyz
                                        statistics[8]++;
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Total\t" + statistics[0]);

                System.out.println("XXX \t" + statistics[1]);
                System.out.println("XXY \t" + statistics[2]);

                System.out.println("ER-XYX\t" + statistics[3]);
                System.out.println("ER-XYY\t" + statistics[4]);
                System.out.println("ER-XYZ\t" + statistics[5]);

                System.out.println("EN-XYX\t" + statistics[6]);
                System.out.println("EN-XYY\t" + statistics[7]);
                System.out.println("EN-XYZ\t" + statistics[8]);
            }

            @Override
            public void processDirChanged(File dir) {
            }
        });

        archiveReader.readDir(new File(ocrCorpusDirectory));

        // Process statistics
        double val1 = (double)statistics[1] / (double)statistics[0];
        double val2 = (double)statistics[2] / (double)statistics[0];
        double val3 = (double)statistics[3] / (double)statistics[0];
        double val4 = (double)statistics[4] / (double)statistics[0];
    }

    private ITrigramTransducer createModelContext(TokenCorpa corpa) throws IOException, ClassNotFoundException {
        final double editDist = 2.0;

        String file = "tmp/generator";
        String mixedTrigramsFile = file + ".mtg";

        // Features
        Map<IFeature, Double> featuresMap = new HashMap<IFeature, Double>();
        featuresMap.put(new SpellCorrectorDistanceTrigramFeature(editDist), 1.0);
        featuresMap.put(SpellCorrectorMixedTrigramFeature.loadFeature(mixedTrigramsFile, corpa), 1.0);

        // Our model
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(corpa, editDist, featuresMap);

        return transducer;
    }

    private ITrigramTransducer createModelIsolated(TokenCorpa corpa) {
        final double editDist = 2.0;

        // Features
        Map<IFeature, Double> featuresMap = new HashMap<IFeature, Double>();
        featuresMap.put(new SpellCorrectorDistanceTrigramFeature(editDist), 1.0);

        // Our model
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(corpa, editDist, featuresMap);

        return transducer;
    }
}
