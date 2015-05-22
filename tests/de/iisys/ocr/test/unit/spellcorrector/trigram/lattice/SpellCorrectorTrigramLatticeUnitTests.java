package de.iisys.ocr.test.unit.spellcorrector.trigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.spellcorrector.trigram.SpellCorrectorTrigramTransducer;
import de.iisys.ocr.spellcorrector.trigram.lattice.SpellCorrectorTrigramLattice;
import de.iisys.ocr.test.unit.spellcorrector.trigram.SpellCorrectorTransducerTrigramUnitTests;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNodeList;
import de.iisys.ocr.types.DoubleVector;
import de.iisys.ocr.types.Token;
import org.junit.Test;

import java.util.Map;

/**
 * SpellCorrectorTrigramLatticeUnitTests
 * de.iisys.ocr.test.unit.spellcorrector.trigram.lattice
 * Created by reza on 16.10.14.
 */
public class SpellCorrectorTrigramLatticeUnitTests extends SpellCorrectorTransducerTrigramUnitTests {
    @Test
    public void testLatticeIterator() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input = "nhis aged portin of society were distinguished frow .";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens);

        int i = 0;
        String[] ch = {"", "", "niis", "aged", "pntkm", "at", "society", "were", "distinguished", "frow", ".", "", ""};
        for (ITrigramLatticeNodeList latticeNodes : lattice) {
            int s = (latticeNodes.size() == 1? 1 : latticeNodes.size()+1);
            System.out.print(s + " " + ch[i++] + " ");
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
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens, outputTokens);

        //lattice.computeAlphaBetaUnlabeled();

        lattice.computeAlphaBetaLabeled();
    }

    @Test
    public void testComputeViterbi() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input =  "AEine AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String output =  "Eine Aline Aline";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens, outputTokens);

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
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens, outputTokens);

        DoubleVector empirical = lattice.computeFeaturesEmpiricalValue();

        assert empirical.size() == mFeaturesMap.size();
        assert empirical.get(0) == 1.0;
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
        final ITrigramTransducer transducer = new SpellCorrectorTrigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorTrigramLattice lattice = new SpellCorrectorTrigramLattice(transducer, inputTokens, outputTokens);

        lattice.computeAlphaBetaUnlabeled();
        DoubleVector estimated = lattice.computeFeaturesEstimatedValue();

        assert estimated.size() == mFeaturesMap.size();
        assert estimated.get(0) == 0.0;
        assert estimated.get(1) > 0.0;
    }
}
