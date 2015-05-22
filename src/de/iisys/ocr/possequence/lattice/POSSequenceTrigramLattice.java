package de.iisys.ocr.possequence.lattice;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.StartNode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.possequence.POSSequenceTrigramTransducer;
import de.iisys.ocr.types.SparseVector;

import java.util.*;

/**
 * POSSequenceTrigramLattice
 * Created by reza on 23.01.15.
 */
public class POSSequenceTrigramLattice extends ArrayList<Collection<LatticeNode>> {
    private final POSSequenceTrigramTransducer transducerModel;
    private final List<String> inputTokens;
    private final List<Short> outputTokens;
    private final SparseVector[] propertiesVectors;
    private final int length;

    private POSSequenceTrigramLattice(POSSequenceTrigramTransducer transducerModel, SparseVector[] propertiesVectors,
                                      List<String> inputTokens, List<Short> outputTokens) {
        this.transducerModel = transducerModel;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.length = propertiesVectors.length;
        this.propertiesVectors = propertiesVectors;
    }

    // <editor-fold desc="Alpha-Beta">
    public double alpha(/* Corpa corpa*/) {
        Collection<LatticeNode> prevList2 = null;
        Collection<LatticeNode> prevList1 = get(0);
        Collection<LatticeNode> currentList = get(1);
        for (int j = 2; j < size(); j++) {
            //System.out.println("------ j=" + j);
            prevList2 = prevList1;
            prevList1 = currentList;
            currentList = get(j);

            for (LatticeNode s : currentList) {
                for (LatticeNode s1 : prevList1) {
                    // z_i and max(z_i)
                    double[] values = new double[prevList2.size()];
                    int valueIndex = 0;
                    double maxValue = Double.NEGATIVE_INFINITY;

                    for (LatticeNode s2 : prevList2) {
                        double prevLogAlpha = s1.getLogAlpha(s2);

                        double value;
                        if (prevLogAlpha != Double.NEGATIVE_INFINITY) {
                            double logCliqueValue = transducerModel.getLogCliqueValue(s2, s1, s, propertiesVectors, j);
                            //System.out.println("   - " + corpa.getPOSById(s2.getPosID()) + "_" + corpa.getPOSById(s1.getPosID()) + "_" + corpa.getPOSById(s.getPosID()) + ": " + logCliqueValue);

                            value = prevLogAlpha + logCliqueValue;
                        } else {
                            value = Double.NEGATIVE_INFINITY;
                        }

                        // Store values of z_i and max(z_i)
                        values[valueIndex++] = value;
                        if (value > maxValue) maxValue = value;
                    }

                    // compute log(α)
                    double logAlpha = logSumExp(values, maxValue);
                    s.setLogAlpha(s1, logAlpha);

                    //System.out.println("*_" + corpa.getPOSById(s1.getPosID()) + "_" + corpa.getPOSById(s.getPosID()) + " = " + logAlpha);
                }
            }
        }

        /* Alpha partition value */
        LatticeNode endNode1 = ((List<LatticeNode>)get(length-2)).get(0);
        LatticeNode endNode2 = ((List<LatticeNode>)get(length-1)).get(0);

        return endNode2.getLogAlpha(endNode1);
    }

    public double beta(/*Corpa corpa*/) {
        Collection<LatticeNode> nextList2 = null;
        Collection<LatticeNode> nextList1 = get(length-1);
        Collection<LatticeNode> currentList = get(length-2);

        for (int j = length - 3; j >= 0; j--) {
            //System.out.println("------ j=" + (j+2));
            nextList2 = nextList1;
            nextList1 = currentList;
            currentList = get(j);

            for (LatticeNode s : currentList) {
                for (LatticeNode s1 : nextList1) {
                    // z_i and max(z_i)
                    double[] values = new double[nextList2.size()];
                    int valueIndex = 0;
                    double maxValue = Double.NEGATIVE_INFINITY;

                    for (LatticeNode s2 : nextList2) {
                        double nextLogBeta = s2.getLogBeta(s1);

                        double value;
                        if (nextLogBeta != Double.NEGATIVE_INFINITY) {
                            double logCliqueValue = transducerModel.getLogCliqueValue(s, s1, s2, propertiesVectors, j + 2);
                            //System.out.println("   - " + corpa.getPOSById(s.getPosID()) + "_" + corpa.getPOSById(s1.getPosID()) + "_" + corpa.getPOSById(s2.getPosID()) + ": " + logCliqueValue);

                            value = nextLogBeta + logCliqueValue;
                        } else {
                            value = Double.NEGATIVE_INFINITY;
                        }

                        // Store values of z_i and max(z_i)
                        values[valueIndex++] = value;
                        if (value > maxValue) maxValue = value;
                    }

                    // compute log(β)
                    double logBeta = logSumExp(values, maxValue);
                    s1.setLogBeta(s, logBeta);

                    //System.out.println(corpa.getPOSById(s.getPosID()) + "_" + corpa.getPOSById(s1.getPosID()) + "_* = " + logBeta);
                }
            }
        }

        /* Beta partition value */
        LatticeNode startNode1 = ((List<LatticeNode>)get(1)).get(0);
        LatticeNode startNode2 = ((List<LatticeNode>)get(0)).get(0);

        return startNode1.getLogBeta(startNode2);
    }

    // </editor-fold>

    // <editor-fold desc="Train">

    public double updateFeatureDerivatives(double[] initialDerivatives) {
        double Z_beta = beta();
        double Z_alpha = alpha();
        assert Math.abs(Z_beta - Z_alpha) < 1E-5;

        double[] empiricalValues = computeFeaturesEmpiricalValue();

        double[] estimatedValues = computeFeaturesEstimatedValue(Z_alpha);

        int m = transducerModel.getFeatureCount();
        for (int i = 0; i < m; i++) {
            initialDerivatives[i] += empiricalValues[i] - estimatedValues[i]; // TODO: add penalized
        }

        return Z_beta;
    }

    public double[] computeFeaturesEmpiricalValue() {
        int nFeatures = transducerModel.getFeatureCount();
        double[] empirical_f = new double[nFeatures];

        short prevNode2;
        short prevNode1 = outputTokens.get(0);
        short currentNode = outputTokens.get(1);
        for (int j = 2; j < length; j++) {
            prevNode2 = prevNode1;
            prevNode1 = currentNode;
            currentNode = outputTokens.get(j);

            for (int i = 0; i < nFeatures; i++) {
                double f_i = transducerModel.getFeatureValue(i, prevNode2, prevNode1, currentNode, propertiesVectors, j);

                empirical_f[i] += f_i;
            }
        }

        return empirical_f;
    }

    public double[] computeFeaturesEstimatedValue(double log_Z_lambda/*, Corpa corpa*/) {
        int nFeatures = transducerModel.getFeatureCount();
        double[] estimated_f = new double[nFeatures];

        // compute value for the lattice for j = 1 ... |T|
        Collection<LatticeNode> prevList2 = null;
        Collection<LatticeNode> prevList1 = get(0);
        Collection<LatticeNode> currentList = get(1);
        for (int j = 2; j < size(); j++) {
            //System.out.println("------ j=" + j);
            prevList2 = prevList1;
            prevList1 = currentList;
            currentList = get(j);

            for (LatticeNode prevNode2 : prevList2) { // for values of s
                for (LatticeNode prevNode1 : prevList1) { // for values of s'
                    for (LatticeNode currentNode : currentList) { // for values of s"
                        /*System.out.println(corpa.getPOSById(prevNode2.getPosID()) + " -> " +
                                corpa.getPOSById(prevNode1.getPosID()) + " " +
                                corpa.getPOSById(currentNode.getPosID()));*/

                        for (int i = 0; i < nFeatures; i++) {
                            // f_i(s,s',s",x,j)
                            double f_i = transducerModel.getFeatureLogValue(i, prevNode2, prevNode1, currentNode, propertiesVectors, j);

                            if (f_i != 0.0) {
                                // log α_{j-1}(s, s' | x)
                                double log_alpha_j_1 = prevNode1.getLogAlpha(prevNode2);

                                // log Ψ_j(x, s, s', s")
                              double logCliqueValue = transducerModel.getLogCliqueValue(prevNode2, prevNode1, currentNode, propertiesVectors, j);

                                // log β_{j}(s', s" | x)
                                double log_beta_j = currentNode.getLogBeta(prevNode1);

                                // Computing ( f_i(s,s',s",x,j) * α_{j-1}(s, s' | x) * Ψ_j(x, s, s', s'') * β_{j}(s', s'' | x) ) / Z_λ
                                estimated_f[i] += f_i * Math.exp(log_alpha_j_1 + logCliqueValue + log_beta_j - log_Z_lambda);
                            }
                        }
                    }
                }
            }
        }

        return estimated_f;
    }

    // </editor-fold>

    // <editor-fold desc="Viterbi">
    public List<Short> viterbi() {
        Collection<LatticeNode> prevList2 = null;
        Collection<LatticeNode> prevList1 = get(0);
        Collection<LatticeNode> currentList = get(1);
        for (int j = 2; j < size(); j++) {
            //System.out.println("------ j=" + j);
            prevList2 = prevList1;
            prevList1 = currentList;
            currentList = get(j);

            for (LatticeNode s : currentList) {
                for (LatticeNode s1 : prevList1) {
                    double logDelta = Double.NEGATIVE_INFINITY; // invalid value
                    LatticeNode prevPsiNode = null;

                    for (LatticeNode s2 : prevList2) {
                        double prevLogDelta = s1.getLogDelta(s2);

                        double cliqueValue = transducerModel.getLogCliqueValue(s2, s1, s, propertiesVectors, j);

                        double newDelta = prevLogDelta + cliqueValue;

                        if (newDelta > logDelta) {
                            logDelta = newDelta;
                            prevPsiNode = s2;
                        }
                        //System.out.println(corpa.getPOSById(s2.getPosID()) + "_" + corpa.getPOSById(s1.getPosID()) + "_" + corpa.getPOSById(s.getPosID()) + " = " + newDelta);
                    }

                    s.setLogDelta(s1, logDelta);
                    s.setPrevPsiNode(s1, prevPsiNode);
                }
            }
        }

        /* Viterbi inference */
        LatticeNode currentNode = ((List<LatticeNode>)currentList).get(0);
        LatticeNode prevNode = ((List<LatticeNode>)prevList1).get(0);
        Short result[] = new Short[length];
        int j = length - 1;
        while (prevNode != null) {
            result[j--] = currentNode.getPosID();
            LatticeNode prevNode2 = currentNode.getPrevPsiNode(prevNode);
            currentNode = prevNode;
            prevNode = prevNode2;
        }
        result[j] = currentNode.getPosID();

        return Arrays.asList(result);
    }

    public List<List<Short>> viterbiKBest(List<Short> maximum) {
        // Maximum sequence nodes
        LatticeNode maximumSeq[] = new LatticeNode[length];
        LatticeNode currentNode = maximumSeq[length-1] = ((List<LatticeNode>)get(size()-1)).get(0);
        LatticeNode prevNode = maximumSeq[length-2] = ((List<LatticeNode>)get(size()-2)).get(0);
        double maxSeqDelta = currentNode.getLogDelta(prevNode);

        int j_s = length - 3;
        while (j_s >= 0 && prevNode != null) {
            LatticeNode prevNode2 = currentNode.getPrevPsiNode(prevNode);
            maximumSeq[j_s--] = prevNode2;
            currentNode = prevNode;
            prevNode = prevNode2;
        }


        // Find k-best sequences
        SortedMap<Double, List<Short>> sequences = new TreeMap<Double, List<Short>>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o2.compareTo(o1);
            }
        });
        //Collection<LatticeNode> prevList2 = null;
        //Collection<LatticeNode> prevList1 = get(0);
        //Collection<LatticeNode> currentList = get(1);
        for (int j = 2; j < size()-2; j++) {
            //System.out.println("------ j=" + j);
            //prevList2 = prevList1;
            //prevList1 = currentList;
            Collection<LatticeNode> currentList = get(j);

            // Check to see if we can manipulate the sequence
            if (currentList.size() > 1) {
                LatticeNode maxNodeCurrent = maximumSeq[j];
                LatticeNode maxNodePrev1 = maximumSeq[j-1];
                LatticeNode maxNodePrev2 = maximumSeq[j-2];
                LatticeNode maxNodeNext1 = maximumSeq[j+1];
                LatticeNode maxNodeNext2 = maximumSeq[j+2];

                double secondValue = Double.NEGATIVE_INFINITY;
                LatticeNode secondNode = null;

                for (LatticeNode s : currentList) {
                    if (s == maxNodeCurrent) continue;

                    double cliqueValue1 = transducerModel.getLogCliqueValue(
                            maxNodePrev2, maxNodePrev1, s, propertiesVectors, j);
                    double cliqueValue2 = transducerModel.getLogCliqueValue(
                            maxNodePrev1, s, maxNodeNext1, propertiesVectors, j+1);
                    double cliqueValue3 = transducerModel.getLogCliqueValue(
                            s, maxNodeNext1, maxNodeNext2, propertiesVectors, j+2);

                    double value = cliqueValue1 + cliqueValue2 + cliqueValue3;

                    if (value > secondValue) {
                        secondValue = value;
                        secondNode = s;
                    }
                }

                // Create the modified sequence
                List<Short> sequence = new ArrayList<Short>(maximum);
                sequence.set(j, secondNode.getPosID());

                // Calculate new sequence delta
                double cliqueValue1 = transducerModel.getLogCliqueValue(
                        maxNodePrev2, maxNodePrev1, maxNodeCurrent, propertiesVectors, j);
                double cliqueValue2 = transducerModel.getLogCliqueValue(
                        maxNodePrev1, maxNodeCurrent, maxNodeNext1, propertiesVectors, j+1);
                double cliqueValue3 = transducerModel.getLogCliqueValue(
                        maxNodeCurrent, maxNodeNext1, maxNodeNext2, propertiesVectors, j+2);
                double secondSeqDelta = maxSeqDelta - (cliqueValue1+cliqueValue2+cliqueValue3) + secondValue;

                // Add to list
                sequences.put(secondSeqDelta, sequence);
            }
        }

        List<List<Short>> result = new ArrayList<List<Short>>(sequences.values());
        return result;
    }

    // </editor-fold>

    // <editor-fold desc="Inference">

    public double computeSequenceProbability(List<Short> output, double log_Z_lambda) {
        double estimated = 0;

        for (int j = 2; j< length; j++) {
            short s = output.get(j-2);
            short s1 = output.get(j-1);
            short s2 = output.get(j);

            double logCliqueValue = transducerModel.getLogCliqueValue(s, s1, s2, propertiesVectors, j);

            estimated += logCliqueValue;
        }

        // divide in log space
        estimated -= log_Z_lambda;

        return estimated;
    }

    public double computeOutputSequenceProbability(double log_Z_lambda) {
        return computeSequenceProbability(outputTokens, log_Z_lambda);
    }

    // </editor-fold>

    // <editor-fold desc="Other">

    public void clearLattice() {
        for (Collection<LatticeNode> latticeNodes : this) {
            for (LatticeNode latticeNode: latticeNodes) {
                latticeNode.clear();
            }
        }
    }

    protected double logSumExp(double[] values, double maxValue) {
        if (values.length == 1) {
            return values[0];
        }
        double sumValue = 0.0;
        for (int s1 = 0; s1 < values.length; s1++) {
            sumValue += Math.exp(values[s1] - maxValue);
        }

        return maxValue + Math.log(sumValue);
    }

    public void printLattice(Corpa corpa){
        for (Collection<LatticeNode> latticeColumn : this) {
            for (LatticeNode posNode : latticeColumn) {
                short posID = posNode.getPosID();
                System.out.print(" " + posID + "-" + corpa.getPOSById(posID));
            }
            System.out.print("\n");
        }
        System.out.println();
    }

    public static POSSequenceTrigramLattice generateLattice(TokenCorpa corpa, POSSequenceTrigramTransducer transducerModel,
                                                    List<String> inputTokens, List<Short> outputTokens){
        List<String> inputs = new ArrayList<String>();
        inputs.add(null);
        inputs.add(null);
        inputs.addAll(inputTokens);
        inputs.add(null);
        inputs.add(null);
        SparseVector[] propertiesVectors = transducerModel.createPropertiesVector(inputs);
        POSSequenceTrigramLattice lattice = new POSSequenceTrigramLattice(transducerModel, propertiesVectors, inputs, outputTokens);
        lattice.add(Arrays.asList(new LatticeNode(StartNode.SHORT_INDEX, null)));
        int index = 0;
        lattice.add(Arrays.asList(new LatticeNode(StartNode.SHORT_INDEX, lattice.get(index++))));
        for (String inputToken : inputTokens) {
            Set<Short> posIDs = transducerModel.buildCandidatePOSList(inputToken);
            if (posIDs.size() == 0) {
                posIDs = corpa.getAllPosIDs();
            }

            List<LatticeNode> posNodes = new ArrayList<LatticeNode>();
            for (short posID : posIDs) posNodes.add(new LatticeNode(posID, lattice.get(index)));
            lattice.add(posNodes);
            index++;
        }
        lattice.add(Arrays.asList(new LatticeNode(EndNode.SHORT_INDEX, lattice.get(index++))));
        lattice.add(Arrays.asList(new LatticeNode(EndNode.SHORT_INDEX, lattice.get(index))));

        return lattice;
    }

    // </editor-fold>
}
