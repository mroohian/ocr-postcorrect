package de.iisys.ocr.test.unit.spellcorrector.trigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.spellcorrector.trigram.SpellCorrectorTrigramTransducer;
import de.iisys.ocr.spellcorrector.trigram.features.SpellCorrectorDistanceTrigramFeature;
import de.iisys.ocr.spellcorrector.trigram.lattice.SpellCorrectorTrigramLattice;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.WordPOSCorpaUnit;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import de.iisys.ocr.types.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SpellCorrectorTest
 * Created by reza on 28.01.15.
 */
public class SpellCorrectorTest extends BaseTest {
    private static WordPOSCorpaUnit mWordPOSCorpaUnit;

    @BeforeClass
    public static void setup() throws IOException, ClassNotFoundException {
        mWordPOSCorpaUnit = new WordPOSCorpaUnit(mPOSAlphabetFile, mWordAlphabetFile, mWordPOSMappingFile);
    }

    protected Map<IFeature, Double> featureMap(final double editDist) {
        HashMap<IFeature, Double> featuresMap = new HashMap<IFeature, Double>();
        featuresMap.put(new SpellCorrectorDistanceTrigramFeature(editDist), 2.0);
        //featuresMap.put(SpellCorrectorTransducerTrigramUnitTests.sampleFeature1, 1.0);
        //featuresMap.put(SpellCorrectorTransducerTrigramUnitTests.sampleFeature2, 1.0);
        return featuresMap;
    }

    @Test
    public void testComputeViterbi() throws IOException, InterruptedException {
        File file = new File("tmp/sample");

        FileReader a = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(a);

        String line = bufferedReader.readLine();
        while (line != null) {
            System.out.println(line);
            line = bufferedReader.readLine();
        }

        Thread.sleep(12343);
    }

    @Test
    public void testComputeViterbi1() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input =  "nhis aged portin of society were distinguished frow .";
        final String inputCh = "niis aged pntkm at society were distinguished frow .";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String output =  "This aged portion of society were distinguished from .";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens, outputTokens);

        lattice.printLattice(20);

        // Input
        System.out.println(inputCh);
        System.out.println();

        // Compute viterbi
        lattice.computeViterbi();
        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " ");
        System.out.println();
    }

}
