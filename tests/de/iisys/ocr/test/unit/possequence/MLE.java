package de.iisys.ocr.test.unit.possequence;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.optimizer.*;
import de.iisys.ocr.possequence.POSSequenceTrigramLikelihoodFunction;
import de.iisys.ocr.possequence.POSSequenceTrigramTransducer;
import de.iisys.ocr.possequence.lattice.POSSequenceTrigramLattice;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.WordPOSCorpaUnit;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.trainingset.TrainingSetGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * MLE
 * Created by reza on 13.01.15.
 */
public class MLE extends BaseTest {
    protected static WordPOSCorpaUnit mWordPOSCorpaUnit;

    private final String input = "Tha peeple will kncw it it the enc ."; // x
    private final String output = "⊥ ⊥ DT NN MD VB PRP IN DT NN . ⊤ ⊤"; // y
    private final double L = 1.5;

    double[] weights = {
            2.0089140902678033,
            -0.049450117774921566,
            1.9914872986447167,
            0.5252354041795523
    };

    @BeforeClass
    public static void setup() throws IOException, ClassNotFoundException {
        mWordPOSCorpaUnit = new WordPOSCorpaUnit(mPOSAlphabetFile, mWordAlphabetFile, mWordPOSMappingFile);

        /*String POSAlphabetFile = System.getProperty("user.dir") + "/tmp/generator-computer.pab";
        String WordAlphabetFile = System.getProperty("user.dir") + "/tmp/generator-computer.wab";
        String WordPOSMappingFile = System.getProperty("user.dir") + "/tmp/generator-computer.wpm";
        mWordPOSCorpaUnit = new WordPOSCorpaUnit(POSAlphabetFile, WordAlphabetFile, WordPOSMappingFile);*/
    }

    @Test
    public void verifyInput() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();
        POSSequenceTrigramTransducer transducerModel = new POSSequenceTrigramTransducer(corpa, L, null);

        /* Sample input */
        String[] inputTokens = corpa.createStringArray(new DelimiterTokenizer(input));

        for (String inputToken : inputTokens) {
            Set<INode> candidateList = transducerModel.buildCandidateList(inputToken);
            for (INode candidate : candidateList) {
                System.out.println(candidate.getWordId() + " " + corpa.getWordById(candidate.getWordId()));
            }
            System.out.println();

            Set<Short> posIDs = transducerModel.buildCandidatePOSList(candidateList);

            System.out.print(inputToken);
            for (short posID : posIDs) {
                System.out.print(" " + posID + "-" + corpa.getPOSById(posID));
            }
            System.out.print("\n");
        }
    }

    @Test
    public void printLattice() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        String input = "nhis aged portin of society were distinguished frow .";

        // Generate model
        final POSSequenceTrigramTransducer transducerModel = TestModel.makeModel(corpa, weights, L);

        /* Sample input */
        List<String> inputTokens = corpa.createStringList(new DelimiterTokenizer(input));

        /* Initialize the lattice*/
        POSSequenceTrigramLattice lattice = POSSequenceTrigramLattice.generateLattice(corpa, transducerModel, inputTokens, null);

        // Print lattice
        lattice.printLattice(corpa);
    }

    @Test
    public void testComputation() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        // Generate model
        final POSSequenceTrigramTransducer transducerModel = TestModel.makeModel(corpa, weights, L);

        /* Sample input */
        List<String> inputTokens = corpa.createStringList(new DelimiterTokenizer(input));

        /* Sample output */
        List<Short> outputTokens = corpa.createPOSList(output);

        /* Initialize the lattice*/
        POSSequenceTrigramLattice lattice = POSSequenceTrigramLattice.generateLattice(corpa, transducerModel, inputTokens, outputTokens);

        // Print lattice
        lattice.printLattice(corpa);

        /* Compute derivatives */
        double[] derivatives = new double[transducerModel.getFeatureCount()];
        double Z_lambda = lattice.updateFeatureDerivatives(derivatives);

        //double prob = lattice.computeSequenceProbability(Z_alpha);
    }

    @Test
    public void train() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        // Generate model
        final POSSequenceTrigramTransducer transducerModel = TestModel.makeModel(corpa, weights, L);

        /* Lattices matrix */
        List<POSSequenceTrigramLattice> latticeInstances = new ArrayList<POSSequenceTrigramLattice>();
        if (true) {
            /* Sample input */
            System.out.println("reading training data...");
            //List<String> files = Arrays.asList("tmp/train/generator.train264", "tmp/train/generator.train001");
            /*List<String> files = Arrays.asList("tmp/train/generator.train001");*/
            List<String> files = Arrays.asList(
                "tmp/train/generator.train001",
                "tmp/train/generator.train002",
                "tmp/train/generator.train003",
                "tmp/train/generator.train004",
                "tmp/train/generator.train005",
                "tmp/train/generator.train006",
                "tmp/train/generator.train007",
                "tmp/train/generator.train008",
                "tmp/train/generator.train009",
                "tmp/train/generator.train010"
            );
            Map<List<String>, List<Short>> instances = TrainingSetGenerator.readTrainingFiles(files, corpa);

            /* Generate lattices */
            System.out.println("generation lattices...");
            for (List<String> inputTokens : instances.keySet()) { // x
                List<Short> outputTokens = instances.get(inputTokens); // y

                /* Initialize the lattice */
                POSSequenceTrigramLattice lattice = POSSequenceTrigramLattice.generateLattice(corpa, transducerModel, inputTokens, outputTokens);
                latticeInstances.add(lattice);
            }
        }

        /* Training */
        System.out.println("training...");
        for (int iter = 0; iter < 1000; iter++) {
            /* Feature derivatives matrix */
            int m = transducerModel.getFeatureCount();
            double[] derivatives = new double[m];
            double functionValue = 0;

            for (POSSequenceTrigramLattice lattice : latticeInstances) { // x
                lattice.clearLattice();
                /* Print lattice */
                //lattice.printLattice(corpa);

                double z_lambda = lattice.updateFeatureDerivatives(derivatives);

                functionValue += lattice.computeOutputSequenceProbability(z_lambda);

                //System.out.print("test");
            }

            /* Optimization step */
            System.out.println("iteration: " + iter);
            System.out.println("F: " + functionValue);
            System.out.println(String.format("Prob: %.5f %%", Math.exp(functionValue) * 100.0));
            System.out.println("G: " + derivatives[0] + ", " + derivatives[1]);
            double step = 0.0000001;
            for (int i = 0; i < m; i++) {
                double featureWeight = transducerModel.getFeatureWeights(i);

                featureWeight += step * derivatives[i];

                transducerModel.setFeatureWeights(i, featureWeight);
                System.out.println("lambda_" + i + ": " + featureWeight);
            }
            System.out.println();
        }
    }

    @Test
    public void trainLBFG() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        // Generate model
        final POSSequenceTrigramTransducer transducerModel = TestModel.makeModel(corpa, weights, L);

        /* Lattices matrix */
        final List<POSSequenceTrigramLattice> latticeInstances = new ArrayList<POSSequenceTrigramLattice>();
        if (true) {
            /* Sample input */
            System.out.println("reading training data...");
            //List<String> files = Arrays.asList("tmp/train/generator.train001");
            List<String> files = Arrays.asList(
                    "tmp/train/generator.train001",
                    "tmp/train/generator.train002",
                    "tmp/train/generator.train003",
                    "tmp/train/generator.train004",
                    "tmp/train/generator.train005",
                    "tmp/train/generator.train006",
                    "tmp/train/generator.train007",
                    "tmp/train/generator.train008",
                    "tmp/train/generator.train009",
                    "tmp/train/generator.train010"
            );
            Map<List<String>, List<Short>> instances = TrainingSetGenerator.readTrainingFiles(files, corpa);

            /* Generate lattices */
            System.out.println("generation lattices...");
            for (List<String> inputTokens : instances.keySet()) { // x
                List<Short> outputTokens = instances.get(inputTokens); // y

                /* Initialize the lattice */
                POSSequenceTrigramLattice lattice = POSSequenceTrigramLattice.generateLattice(corpa, transducerModel, inputTokens, outputTokens);
                latticeInstances.add(lattice);
            }
        }

        /* Training */
        System.out.println("training...");
        IFunction function = new POSSequenceTrigramLikelihoodFunction(transducerModel, latticeInstances);
        function.getValue();

        //IOptimizer optimizer = new OWLQNOptimizer(function);
        IOptimizer optimizer = new QNMinimizerOptimizer(function);
        optimizer.optimize();
    }

    @Test
    public void test() throws IOException, ClassNotFoundException {
        final TokenCorpa corpa = mWordPOSCorpaUnit.getCorpa();

        // TODO: remove
        final String input = "nhis aged portin of society were distinguished frow .";
        final String inputCh = "niis aged pntkm at society were distinguished frow .";

        // Generate model
        //final POSSequenceTrigramTransducer transducerModel = TestModel.makeModel(corpa, weights, L);
        final POSSequenceTrigramTransducer transducerModel = TestModel1.makeModel(corpa, null, L);

        /* Sample input */
        List<String> inputTokens = corpa.createStringList(new DelimiterTokenizer(input));

        /* Initialize the lattice*/
        POSSequenceTrigramLattice lattice = POSSequenceTrigramLattice.generateLattice(corpa, transducerModel, inputTokens, null);

        // Print lattice
        lattice.printLattice(corpa);

        // Input
        System.out.println(inputCh);
        System.out.println();

        // Viterbi
        List<Short> result = lattice.viterbi();
        for (short posID : result) {
            System.out.print(corpa.getPOSById(posID) + " ");
        }
        System.out.println();

        // Viterbi K-Best results
        System.out.println("K-Best:");
        List<List<Short>> kBestResult = lattice.viterbiKBest(result);
        for (List<Short> kResult : kBestResult) {
            for (short posID : kResult) {
                System.out.print(corpa.getPOSById(posID) + " ");
            }
            System.out.println();
        }
    }

}
