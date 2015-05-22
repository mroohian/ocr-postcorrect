package de.iisys.ocr.test.unit.spellcorrector.bigram;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.optimizer.*;
import de.iisys.ocr.spellcorrector.bigram.SpellCorrectorBigramTransducer;
import de.iisys.ocr.spellcorrector.bigram.features.SpellCorrectorBigramRankFeature;
import de.iisys.ocr.spellcorrector.bigram.features.SpellCorrectorDistanceBigramFeature;
import de.iisys.ocr.spellcorrector.bigram.features.SpellCorrectorGraphBigramFeature;
import de.iisys.ocr.spellcorrector.bigram.lattice.SpellCorrectorBigramLattice;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.DeReWoCorpaUnit;
import de.iisys.ocr.test.core.corpa.WordPOSCorpaUnit;
import de.iisys.ocr.test.core.graph.LocalGraphDBUnit;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.transducer.bigram.IBigramTransducer;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.types.Token;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SpellCorrectorTransducerBigramUnitTests
 * de.iisys.ocr.test.unit.spellcorrector.bigram
 * Created by reza on 04.09.14.
 */
public class SpellCorrectorTransducerBigramUnitTests extends BaseTest {
    protected static LocalGraphDBUnit mGraphDbUnit;
    protected static WordPOSCorpaUnit mWordPOSCorpaUnit;

    protected Map<IFeature, Double> featureMap(final double editDist) {
        HashMap<IFeature, Double> featuresMap = new HashMap<IFeature, Double>();
        featuresMap.put(new SpellCorrectorDistanceBigramFeature(editDist), 2.0);
        featuresMap.put(new SpellCorrectorBigramRankFeature(), 1.0);
        featuresMap.put(new SpellCorrectorGraphBigramFeature(mGraphDbUnit.getGraphDB(), mWordPOSCorpaUnit.getCorpa()), 1.0);
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

        final double L = 1.6;
        final String input =  "AEine Insel ist Feine in einem MeerB . J�hrige In sel . Wir werdEn gehen .";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));

        // Features
        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens);

        // Train model
        final String output =  "Eine Insel ist eine in einem Meer . Jährige In sel . Wir werden gehen .";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));
        lattice.setOutput(outputTokens);
        IOptimizer optimizer = new LBFGSOptimizer(new SequenceLogLikelihoodFunction(transducer, true, lattice));
        optimizer.optimize();

        // Compute viterbi
        lattice.computeViterbi();
        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " ");
        System.out.println();

        //lattice.printLattice(40);
    }

    @Test
    public void testSpellCorrectorUnlabeledTransducer() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input = "AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));

        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens);
        lattice.computeAlphaBetaUnlabeled();
        lattice.computeViterbi();

        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " ");
        System.out.println();

        //lattice.printLattice(40);
    }

    @Test
    public void testSpellCorrectorLabeledTransducer() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input = "AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String output = "Eine Aline";
        Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));

        Map<IFeature, Double> mFeaturesMap = featureMap(L);

        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens, outputTokens);
        lattice.computeAlphaBetaLabeled();
        lattice.computeViterbi();

        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " ");
        System.out.println();

        //lattice.printLattice(40);
    }

    @Test
    public void testSimpleTransducer() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input =  "AEine AEine";
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));
        final String sampleOutput =  "Eine Aline";
        Token[] sampleOutputTokens = corpa.createTokenArray(new DelimiterTokenizer(sampleOutput));

        // Features
        Map<IFeature, Double> mFeaturesMap = new HashMap<IFeature, Double>();
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F1";
            }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (prevNode1 == null && node != null && corpa.getWordById(node.getNode().getWordId()).equals("Eine") ? 1 : 0);
            }
        }, 1.5);
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F2";
            }

            @Override
            public double computeValue(Object[] extras,String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (node == null && prevNode1 != null && corpa.getWordById(prevNode1.getNode().getWordId()).equals("Aline") ? 1 : 0);
            }
        }, 1.5);
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F3";
            }

            @Override
            public double computeValue(Object[] extras,String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (node != null && prevNode1 != null && corpa.getWordById(prevNode1.getNode().getWordId()).equals("Eine") && corpa.getWordById(node.getNode().getWordId()).equals("Aline") ? 1 : 0);
            }
        }, 1.0);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        final SpellCorrectorBigramLattice lattice = new SpellCorrectorBigramLattice(transducer, inputTokens, sampleOutputTokens);
        lattice.printLattice(40);

        // Log likelihood function
        IFunction logLikelihood = new SequenceLogLikelihoodFunction(transducer, true, lattice);

        // Compute viterbi
        lattice.computeViterbi();
        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " ");
        System.out.print("\n\n");

        // Compute labeled lattice
        final String[] possibleOutputs = { "Eine Aline", "Eine Eine", "Aline Eine", "Aline Aline", };
        double sumProb = 0.0;
        for (String output  : possibleOutputs) {
            lattice.reset();
            Token[] outputTokens = corpa.createTokenArray(new DelimiterTokenizer(output));
            lattice.setOutput(outputTokens);
            double ret1 = logLikelihood.getValue();
            //lattice.printLattice(40);

            double confidence = logLikelihood.getValueConfidence();
            System.out.println(String.format("Confidence distribution for [%s]: %.2f%% (value=%.2f)", output, confidence*100.0, ret1));

            sumProb += confidence;
        }
        System.out.println(String.format("Sum distribution: %.2f%%", sumProb*100));
        System.out.println();
    }

    @Test
    public void testSimpleTransducerOptimize() {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        final double L = 1.6;
        final String input1 =  "AEine AEine";
        String[] inputTokens1 = corpa.createStringArray(new DelimiterTokenizer(input1));
        final String output1 = "Eine Aline";
        Token[] outputTokens1 = corpa.createTokenArray(new DelimiterTokenizer(output1));

        final String input2 =  "AEine AEine AEine AEine";
        String[] inputTokens2 = corpa.createStringArray(new DelimiterTokenizer(input2));
        final String output2 = "Eine Aline Eine Aline";
        Token[] outputTokens2 = corpa.createTokenArray(new DelimiterTokenizer(output2));

        // Features
        Map<IFeature, Double> mFeaturesMap = new HashMap<IFeature, Double>();
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F1";
            }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (prevNode1 == null && node != null && corpa.getWordById(node.getNode().getWordId()).equals("Eine") ? 1 : 0);
            }
        }, 0.0);
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F2";
            }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (node == null && prevNode1 != null && corpa.getWordById(prevNode1.getNode().getWordId()).equals("Aline") ? 1 : 0);
            }
        }, 0.0);
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F3";
            }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (node != null && prevNode1 != null && corpa.getWordById(prevNode1.getNode().getWordId()).equals("Eine") && corpa.getWordById(node.getNode().getWordId()).equals("Aline") ? 1 : 0);
            }
        }, 0.0);
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F4";
            }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (node != null && prevNode1 != null && corpa.getWordById(prevNode1.getNode().getWordId()).equals("Aline") && corpa.getWordById(node.getNode().getWordId()).equals("Eine") ? 1 : 0);
            }
        }, 0.0);
        mFeaturesMap.put(new IFeature() {
            @Override
            public String getName() {
                return "F5";
            }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (node != null && prevNode1 != null && corpa.getWordById(prevNode1.getNode().getWordId()).equals("Aline") && corpa.getWordById(node.getNode().getWordId()).equals("Aline") ? 1 : 0);
            }
        }, 0.0);

        // Our model
        final IBigramTransducer transducer = new SpellCorrectorBigramTransducer(mWordPOSCorpaUnit.getCorpa(), L, mFeaturesMap);

        // Compute unlabeled lattice
        final SpellCorrectorBigramLattice lattice1 = new SpellCorrectorBigramLattice(transducer, inputTokens1, outputTokens1);
        final SpellCorrectorBigramLattice lattice2 = new SpellCorrectorBigramLattice(transducer, inputTokens2, outputTokens2);

        // Log likelihood function
        final IFunction logLikelihoodFunc = new SequenceLogLikelihoodFunction(transducer, true, lattice1, lattice2);
        //final IFunction logLikelihoodFunc = new SequenceLogLikelihoodFunction(transducer, false, lattice1, lattice2);

        // Train using mallet optimizer
        //IOptimizer optimizer = new OWLQNOptimizer(logLikelihoodFunc);
        IOptimizer optimizer = new LBFGSOptimizer(logLikelihoodFunc);
        try {
            optimizer.optimize();
        } catch (RuntimeException ex) {
            System.err.println(ex.getMessage());
        }

        /*// Train for selected output
        final int iterations = 300;
        final double sigma_2 = 1.0;
        final int nParams = logLikelihoodFunc.getNumParams();
        double[] params = new double[nParams];
        double[] grads = new double[nParams];
        logLikelihoodFunc.getParameters(params);
        for (int it = 1; it < iterations; it++) {
            double value = logLikelihoodFunc.getLogValue();
            System.out.print("Value: " + value + ", ");

            logLikelihoodFunc.getValueGradient(grads);
            double scale = new DoubleVector(grads).vectorLength() * 5;

            for (int i = 0; i < nParams; i++) params[i] += grads[i] / scale;
            logLikelihoodFunc.setParameters(params);
        }*/

        // Compute viterbi
        lattice1.computeViterbi();
        INode[] labels = lattice1.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " "); System.out.print("\n\n");

        lattice2.computeViterbi();
        labels = lattice2.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(corpa.getWordById(label.getWordId()) + " "); System.out.print("\n\n");

    }
}
