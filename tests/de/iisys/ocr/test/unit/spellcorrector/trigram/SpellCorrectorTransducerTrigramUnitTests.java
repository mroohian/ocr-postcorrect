package de.iisys.ocr.test.unit.spellcorrector.trigram;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.optimizer.IOptimizer;
import de.iisys.ocr.optimizer.LBFGSOptimizer;
import de.iisys.ocr.optimizer.SequenceLogLikelihoodFunction;
import de.iisys.ocr.spellcorrector.trigram.SpellCorrectorTrigramTransducer;
import de.iisys.ocr.spellcorrector.trigram.features.SpellCorrectorDistanceTrigramFeature;
import de.iisys.ocr.spellcorrector.trigram.lattice.SpellCorrectorTrigramLattice;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.WordPOSCorpaUnit;
import de.iisys.ocr.test.core.graph.LocalGraphDBUnit;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import de.iisys.ocr.types.Token;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SpellCorrectorTransducerTrigramUnitTests
 * de.iisys.ocr.test.unit.spellcorrector.trigram
 * Created by reza on 15.10.14.
 */
public class SpellCorrectorTransducerTrigramUnitTests extends BaseTest {
    protected static LocalGraphDBUnit mGraphDbUnit;
    protected static WordPOSCorpaUnit mWordPOSCorpaUnit;
    private static IFeature sampleFeature1 = new IFeature() {
        @Override
        public String getName() { return "sampleFeature1"; }

        @Override
        public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
            return (prevNode2.getNode().equals(Token.START_NODE) &&
                    prevNode1.getNode().equals(Token.START_NODE) &&
                    node.getNode().getWordId() == mWordPOSCorpaUnit.getCorpa().getWordId("Eine") ? 1 : 0);
        }
    };

    private static IFeature sampleFeature2 = new IFeature() {
        @Override
        public String getName() { return "sampleFeature2"; }

        @Override
        public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
            return (prevNode2.getNode().equals(Token.START_NODE) &&
                    prevNode1.getNode().getWordId() == mWordPOSCorpaUnit.getCorpa().getWordId("Eine") &&
                    node.getNode().getWordId() == mWordPOSCorpaUnit.getCorpa().getWordId("Aline") ? 1 : 0);
        }
    };

    protected Map<IFeature, Double> featureMap(final double editDist) {
        HashMap<IFeature, Double> featuresMap = new HashMap<IFeature, Double>();
        featuresMap.put(new SpellCorrectorDistanceTrigramFeature(editDist), 2.0);
        featuresMap.put(SpellCorrectorTransducerTrigramUnitTests.sampleFeature1, 1.0);
        featuresMap.put(SpellCorrectorTransducerTrigramUnitTests.sampleFeature2, 1.0);
        return featuresMap;
    }

    @BeforeClass
    public static void setup() throws IOException, ClassNotFoundException {
        mWordPOSCorpaUnit = new WordPOSCorpaUnit(mPOSAlphabetFile, mWordAlphabetFile, mWordPOSMappingFile);
        mGraphDbUnit = new LocalGraphDBUnit(mLocalGraphDBPath, mLocalUser, mLocalPassword, false);
    }

    @AfterClass
    public static void tearDown() {
        mGraphDbUnit.dispose();
    }

    @Test
    public void testSpellCorrectorTransducer() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.5;
        final String input =  "Tha peeple will kncw it it the encl .";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens);

        // Train model
        final String output =  "The people will know it in the end .";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));
        lattice.setOutput(outputTokens);
        IOptimizer optimizer = new LBFGSOptimizer(new SequenceLogLikelihoodFunction(transducer, true, lattice));
        optimizer.optimize();

        // Compute viterbi
        lattice.computeViterbi();
        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) {
            System.out.print(mWordPOSCorpaUnit.getCorpa().getWordById(label.getWordId()) + " ");
        }
        System.out.println();

        //lattice.printLattice(40);
    }
}
