package de.iisys.ocr.test.unit.spellcorrector.bigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.spellcorrector.bigram.SpellCorrectorBigramTransducer;
import de.iisys.ocr.spellcorrector.bigram.lattice.SpellCorrectorBigramLattice;
import de.iisys.ocr.test.unit.spellcorrector.bigram.SpellCorrectorTransducerBigramUnitTests;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.transducer.bigram.IBigramTransducer;
import de.iisys.ocr.transducer.bigram.lattice.IBigramLatticeNodeList;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.types.DoubleVector;
import de.iisys.ocr.types.Token;
import org.junit.Test;

import java.util.Map;

/**
 * SpellCorrectorBigramLatticeUnitTests
 * de.iisys.ocr.test.unit.spellcorrector.bigram.lattice
 * Created by reza on 16.10.14.
 */
public class SpellCorrectorBigramLatticeUnitTests extends SpellCorrectorTransducerBigramUnitTests {

    @Test
    public void testLatticeIterator() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 2.6;
        final String input = "AEine Insel ist Feine in einem MeerB . J�hrige In sel . Wir werdEn gehen .";
        //final String input = "niis aged pntkm at society were distinguished frow .";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens);

        for (IBigramLatticeNodeList latticeNodes : lattice) {
            System.out.print(latticeNodes.size() + " ");
            for (ILatticeNode latticeNode : latticeNodes) {
                System.out.print(corpa.getWordById(latticeNode.getNode().getWordId()) + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void testComputeAlphaBeta() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input =  "AEine AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String output =  "Eine Aline Aline";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens, outputTokens);

        //lattice.computeAlphaBetaUnlabeled();

        lattice.computeAlphaBetaLabeled();
    }

    @Test
    public void testComputeViterbi() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input =  "AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String output =  "Eine Aline";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens, outputTokens);

        // Compute viterbi
        lattice.computeViterbi();
        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " ");
        System.out.println();
    }

    @Test
    public void testComputeEmpirical() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input =  "AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String output =  "Eine Aline";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens, outputTokens);

        DoubleVector empirical = lattice.computeFeaturesEmpiricalValue();

        assert empirical.size() == mFeaturesMap.size();
        assert empirical.get(0) == 0.0;
        assert empirical.get(1) == 1.0;
    }

    @Test
    public void testComputeEstimated() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input =  "AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String output =  "Eine Aline";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens, outputTokens);

        lattice.computeAlphaBetaUnlabeled();
        DoubleVector estimated = lattice.computeFeaturesEstimatedValue();

        assert estimated.size() == mFeaturesMap.size();
        assert estimated.get(0) == 0.0;
        assert estimated.get(1) > 0.0;
    }
}
