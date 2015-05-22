package de.iisys.ocr.test.unit.possequence;

import de.iisys.levdistcorpa.alphabet.POSAlphabet;
import de.iisys.levdistcorpa.alphabet.WordAlphabet;
import de.iisys.levdistcorpa.trie.TrieNode;
import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.StartNode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.optimizer.IFunction;
import de.iisys.ocr.optimizer.IOptimizer;
import de.iisys.ocr.optimizer.QNMinimizerOptimizer;
import de.iisys.ocr.possequence.POSSequenceTrigramLikelihoodFunction;
import de.iisys.ocr.possequence.POSSequenceTrigramTransducer;
import de.iisys.ocr.possequence.lattice.POSSequenceTrigramLattice;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.types.MixedTrigramInfo;
import de.iisys.ocr.types.POSTrigramInfo;
import de.iisys.ocr.types.WordPOSInfo;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * MLEBasic
 * Created by reza on 24.01.15.
 */
public class MLEBasic {
    private final String input = "the test case"; // x
    private final String output = "⊥ ⊥ DT NN NN ⊤ ⊤"; // y
    private final String wrongOutput1 = "⊥ ⊥ DT VB NN ⊤ ⊤"; // y1
    private final String wrongOutput2 = "⊥ ⊥ DT JJ NN ⊤ ⊤"; // y2

    private final double L = 1.5;

    double[] weights = {
            2.0084966223002807,
            -0.050956061381393566,
            1.9920244462255225,
            0.5472386684250422
    };

    private TokenCorpa makeBasicCorpa() throws IOException {
        WordAlphabet wordAlphabet = new WordAlphabet();
        final int wThe = wordAlphabet.add("the");
        final int wTest = wordAlphabet.add("test");
        final int wCase = wordAlphabet.add("case");

        POSAlphabet posAlphabet = new POSAlphabet();
        final short posDT = posAlphabet.add("DT");
        final short posNN = posAlphabet.add("NN");
        final short posVB = posAlphabet.add("VB");
        final short posJJ = posAlphabet.add("JJ");

        final TokenCorpa corpa = new TokenCorpa(wordAlphabet, posAlphabet) {
            @Override
            public void load() throws IOException {
                WordPOSInfo wordPOSInfo;

                wordPOSInfo = new WordPOSInfo(wThe);
                wordPOSInfo.addPosTags(posDT);
                addEntry(wordPOSInfo, TrieNode.TRIENODE_FLAG_NORMAL);

                wordPOSInfo = new WordPOSInfo(wTest);
                wordPOSInfo.addPosTags(posNN);
                wordPOSInfo.addPosTags(posVB);
                wordPOSInfo.addPosTags(posJJ);
                addEntry(wordPOSInfo, TrieNode.TRIENODE_FLAG_NORMAL);

                wordPOSInfo = new WordPOSInfo(wCase);
                wordPOSInfo.addPosTags(posNN);
                addEntry(wordPOSInfo, TrieNode.TRIENODE_FLAG_NORMAL);
            }
        };
        corpa.load();

        // Mixed trigrams
        long totalMixedTrigrams = 600;
        Map<Long, MixedTrigramInfo> mixedTrigrams = new HashMap<Long, MixedTrigramInfo>();
        MixedTrigramInfo mixedTrigramInfo;

        mixedTrigramInfo = new MixedTrigramInfo(StartNode.SHORT_INDEX, wThe, posNN);
        mixedTrigramInfo.setFreq(200);
        mixedTrigrams.put(mixedTrigramInfo.getId(), mixedTrigramInfo);
        mixedTrigramInfo = new MixedTrigramInfo(posDT, wTest, posNN);
        mixedTrigramInfo.setFreq(200);
        mixedTrigrams.put(mixedTrigramInfo.getId(), mixedTrigramInfo);
        mixedTrigramInfo = new MixedTrigramInfo(posNN, wCase, EndNode.SHORT_INDEX);
        mixedTrigramInfo.setFreq(200);
        mixedTrigrams.put(mixedTrigramInfo.getId(), mixedTrigramInfo);

        // POS trigrams
        long totalPOSTrigrams = 1000;
        Map<Long, POSTrigramInfo> posTrigrams = new HashMap<Long, POSTrigramInfo>();
        POSTrigramInfo posTrigramInfo;

        posTrigramInfo = new POSTrigramInfo(new short[] {StartNode.SHORT_INDEX, StartNode.SHORT_INDEX, posDT });
        posTrigramInfo.setFreq(200);
        posTrigrams.put(posTrigramInfo.getId(), posTrigramInfo);
        posTrigramInfo = new POSTrigramInfo(new short[] { StartNode.SHORT_INDEX, posDT, posNN });
        posTrigramInfo.setFreq(200);
        posTrigrams.put(posTrigramInfo.getId(), posTrigramInfo);
        posTrigramInfo = new POSTrigramInfo(new short[] { posDT, posNN, posNN });
        posTrigramInfo.setFreq(200);
        posTrigrams.put(posTrigramInfo.getId(), posTrigramInfo);
        posTrigramInfo = new POSTrigramInfo(new short[] { posNN, posNN, EndNode.SHORT_INDEX});
        posTrigramInfo.setFreq(200);
        posTrigrams.put(posTrigramInfo.getId(), posTrigramInfo);
        posTrigramInfo = new POSTrigramInfo(new short[] { posNN, EndNode.SHORT_INDEX, EndNode.SHORT_INDEX });
        posTrigramInfo.setFreq(200);
        posTrigrams.put(posTrigramInfo.getId(), posTrigramInfo);
        return corpa;
    }

    @Test
    public void trainBasic() throws IOException, ClassNotFoundException {
        TokenCorpa corpa = makeBasicCorpa();

        // Generate model
        final POSSequenceTrigramTransducer transducerModel = TestModel.makeModel(corpa, weights, L);

        /* Sample input */
        List<String> inputTokens = corpa.createStringList(new DelimiterTokenizer(input));

        /* Sample output */
        List<Short> outputTokens = corpa.createPOSList(output);
        List<Short> wrongOutputTokens1 = corpa.createPOSList(wrongOutput1);
        List<Short> wrongOutputTokens2 = corpa.createPOSList(wrongOutput2);

        /* Initialize the lattice*/
        POSSequenceTrigramLattice lattice = POSSequenceTrigramLattice.generateLattice(corpa, transducerModel, inputTokens, outputTokens);

        // Print lattice
        lattice.printLattice(corpa);

        /* Training */
        List<POSSequenceTrigramLattice> latticeInstances = Arrays.asList(lattice);
        IFunction function = new POSSequenceTrigramLikelihoodFunction(transducerModel, latticeInstances);
        function.getValue();

        int m = transducerModel.getFeatureCount();
        for (int iter = 0; iter < 400; iter++) {
            System.out.println("iteration: " + iter);

            /* Compute derivatives */
            double[] derivatives = new double[m];
            function.getValueGradient(derivatives);

            /* Compute function value */
            double functionValue = function.getValue();

            /* Optimization step */
            double step = 0.0005;
            double[] params = new double[m];
            for (int i = 0; i < m; i++) {
                double featureWeight = transducerModel.getFeatureWeights(i);

                featureWeight -= step * derivatives[i];

                //transducerModel.setFeatureWeights(i, featureWeight);
                params[i] = featureWeight;
                System.out.println("lambda_" + i + ": " + featureWeight);
            }
            function.setParameters(params);
            System.out.println();
        }

        /* Compute probability of possible sequences */
        double log_Z_lambda = lattice.alpha();

        double logP1 = lattice.computeSequenceProbability(outputTokens, log_Z_lambda);
        double logP2 = lattice.computeSequenceProbability(wrongOutputTokens1, log_Z_lambda);
        double logP3 = lattice.computeSequenceProbability(wrongOutputTokens2, log_Z_lambda);

        double p1 = Math.exp(logP1);
        double p2 = Math.exp(logP2);
        double p3 = Math.exp(logP3);

        assert Math.abs((p1 + p2 + p3) - 1.0) < 1E-5;
    }

    @Test
    public void trainBasicLBFGS() throws IOException, ClassNotFoundException {
        TokenCorpa corpa = makeBasicCorpa();

        // Generate model
        final POSSequenceTrigramTransducer transducerModel = TestModel.makeModel(corpa, weights, L);

        /* Sample input */
        List<String> inputTokens = corpa.createStringList(new DelimiterTokenizer(input));

        /* Sample output */
        List<Short> outputTokens = corpa.createPOSList(output);
        List<Short> wrongOutputTokens1 = corpa.createPOSList(wrongOutput1);
        List<Short> wrongOutputTokens2 = corpa.createPOSList(wrongOutput2);

        /* Initialize the lattice*/
        POSSequenceTrigramLattice lattice = POSSequenceTrigramLattice.generateLattice(corpa, transducerModel, inputTokens, outputTokens);

        // Print lattice
        lattice.printLattice(corpa);

        /* Training */
        List<POSSequenceTrigramLattice> latticeInstances = Arrays.asList(lattice);
        IFunction function = new POSSequenceTrigramLikelihoodFunction(transducerModel, latticeInstances);
        function.getValue();

        IOptimizer optimizer = new QNMinimizerOptimizer(function);
        optimizer.optimize();

        /* Compute probability of possible sequences */
        double log_Z_lambda = lattice.alpha();

        double logP1 = lattice.computeSequenceProbability(outputTokens, log_Z_lambda);
        double logP2 = lattice.computeSequenceProbability(wrongOutputTokens1, log_Z_lambda);
        double logP3 = lattice.computeSequenceProbability(wrongOutputTokens2, log_Z_lambda);

        double p1 = Math.exp(logP1);
        double p2 = Math.exp(logP2);
        double p3 = Math.exp(logP3);

        assert Math.abs((p1 + p2 + p3) - 1.0) < 1E-5;
    }
}
