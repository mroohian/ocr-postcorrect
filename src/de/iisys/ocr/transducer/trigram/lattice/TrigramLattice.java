package de.iisys.ocr.transducer.trigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.transducer.ITransducer;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.lattice.Lattice;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import de.iisys.ocr.types.DoubleVector;
import de.iisys.ocr.types.Token;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * TrigramLattice
 * de.iisys.ocr.transducer.trigram.lattice
 * Created by reza on 15.10.14.
 */
public abstract class TrigramLattice extends Lattice implements ITrigramLattice {
    private final ITrigramTransducer mTransducer;

    // Start 2
    private final ITrigramLatticeNode startLatticeNode2;
    protected final ITrigramLatticeNodeList startList2;

    // Start1
    private final ITrigramLatticeNode startLatticeNode1;
    protected final ITrigramLatticeNodeList startList1;

    // Stop1
    private final ITrigramLatticeNode stopLatticeNode1;
    protected final ITrigramLatticeNodeList stopList2;

    // Stop2
    private final ITrigramLatticeNode stopLatticeNode2;
    protected final ITrigramLatticeNodeList stopList1;

    public TrigramLattice(ITrigramTransducer transducer, String[] input, Object[] extras, INode[] output) {
        super(input, extras, output);

        mTransducer = transducer;

        // Note: the order of calls here is very important

        // Start 2
        startLatticeNode2 = new TrigramLatticeNode(Token.START_NODE, null, true) {
            public String getDetails() { return "⊥"; }
        };
        startList2 = new TrigramLatticeNodeList(-1, 1, startLatticeNode2);

        // Start1
        startLatticeNode1 = new TrigramLatticeNode(Token.START_NODE, startList2, true) {
            public String getDetails() { return "⊥"; }
        };
        startList1 = new TrigramLatticeNodeList(-1, 1, startLatticeNode1);

        // Initialize
        init();
        ITrigramLatticeNodeList lastList = (ITrigramLatticeNodeList)latticeNodes[mLength-1];

        // Stop1
        stopLatticeNode1 = new TrigramLatticeNode(Token.STOP_NODE, lastList, true) {
            public String getDetails() { return "⊤"; }
        };
        stopList1 = new TrigramLatticeNodeList(-1, 1, stopLatticeNode1);

        // Stop2
        stopLatticeNode2 = new TrigramLatticeNode(Token.STOP_NODE, stopList1, true) {
            public String getDetails() { return "⊤"; }
        };
        stopList2 = new TrigramLatticeNodeList(-1, 1, stopLatticeNode2);
    }

    @Override
    protected void resetLatticeNodes() {
        if (stopLatticeNode2 != null && stopLatticeNode1 != null) {
            stopLatticeNode2.setPrevPsiNode(stopLatticeNode1, null);
            stopLatticeNode2.setDelta(stopLatticeNode1, 0.0);
        }
    }

    //<editor-fold desc="[Iterable]">

    public Iterator<ITrigramLatticeNodeList> getIterator(boolean reverse) {
        final int length = latticeNodes.length;
        final int preceding = 2;
        final int ending = 2;
        final int initPos = (reverse ? length - 1 + ending : - preceding);

        return new ListIterator<ITrigramLatticeNodeList>() {
            private int pos = initPos;

            @Override
            public boolean hasNext() {
                return pos < length + ending;
            }

            @Override
            public boolean hasPrevious() {
                return pos >= -preceding;
            }

            private ITrigramLatticeNodeList getListAtPos(int pos) {
                final ITrigramLatticeNodeList nodeArray;

                if (pos == -2) {
                    nodeArray = startList2;
                } else if (pos == -1) {
                    nodeArray = startList1;
                } else if (pos == length) {
                    nodeArray = stopList1;
                } else if (pos == length+1) {
                    nodeArray = stopList2;
                } else {
                    nodeArray = (ITrigramLatticeNodeList)latticeNodes[pos];
                }

                return nodeArray;
            }

            @Override
            public ITrigramLatticeNodeList next() {
                return getListAtPos(pos++);
            }

            @Override
            public ITrigramLatticeNodeList previous() {
                return getListAtPos(pos--);
            }

            @Override
            public int nextIndex() { return pos + 1; }

            @Override
            public int previousIndex() { return pos - 1; }

            @Override
            public void set(ITrigramLatticeNodeList iLatticeNodes) { }

            @Override
            public void add(ITrigramLatticeNodeList iLatticeNodes) { }

            @Override
            public void remove() { }
        };
    }

    @Override
    public Iterator<ITrigramLatticeNodeList> iterator() {
        return getIterator(false);
    }

    @Override
    public ListIterator<ITrigramLatticeNodeList> forwardIterator() {
        return (ListIterator<ITrigramLatticeNodeList>)getIterator(false);
    }

    @Override
    public ListIterator<ITrigramLatticeNodeList> reverseIterator() {
        return (ListIterator<ITrigramLatticeNodeList>)getIterator(true);
    }

    //</editor-fold>

    //<editor-fold desc="[Getter-Setter]">

    @Override
    public ITransducer getTransducer() {
        return mTransducer;
    }

    //</editor-fold>

    //<editor-fold desc="[Alpha-Beta]">

    protected double computeAlphas(INode[] output) {
        Iterator<ITrigramLatticeNodeList> candidateListIterator = this.iterator();
        ITrigramLatticeNodeList prevCandidateList2 = candidateListIterator.next();
        ITrigramLatticeNodeList prevCandidateList1 = candidateListIterator.next();

        // fill the lattice with values of α for j = 0 ... |T|
        int j = 0;
        while (candidateListIterator.hasNext()) {
            ITrigramLatticeNodeList currentCandidateList = candidateListIterator.next();
            for (ITrigramLatticeNode latticeNode : currentCandidateList) {
                // checks if current node is the labeled output.
                boolean currentOutputMatches = output == null || latticeNode.getNode().getWordId() ==
                        (j < 0 ? Token.START_NODE : (j >= output.length ? Token.STOP_NODE : output[j])).getWordId();

                for (ITrigramLatticeNode prevNode1 : prevCandidateList1) {
                    // checks if current and previous node 1 are the labeled outputs.
                    boolean outputMatches = output == null || (currentOutputMatches && prevNode1.getNode().getWordId() ==
                        (j < 1 ? Token.START_NODE : (j > output.length ? Token.STOP_NODE : output[j-1])).getWordId()
                    );

                    double logAlpha;
                    if (outputMatches) {
                        // z_i and max(z_i)
                        double[] values = new double[prevCandidateList2.size()];
                        int valueIndex = 0;
                        double maxValue = Double.NEGATIVE_INFINITY;

                        for (ITrigramLatticeNode prevNode2 : prevCandidateList2) {
                            //System.out.println(prevNode2.getNode().getWord() + " -> " + prevNode1.getNode().getWord() + " " + latticeNode.getNode().getWord() +
                            //        "( +log α_" + (j) + "(" + prevNode2.getNode().getWord() + ", " + prevNode1.getNode().getWord() + ") )");

                            // α_{j-1}(s' s" | x)
                            double prevAlpha = prevNode1.getAlpha(prevNode2);

                            double value;
                            if (prevAlpha != Double.NEGATIVE_INFINITY) {
                                // Ψ_j(x, s", s', s)
                                double logCliqueValue = mTransducer.getLogCliqueValue(j, mExtras, mInput, prevNode2, prevNode1, latticeNode);

                                // compute α_{j-1} (s' | s", x) * Ψ_j(x, s", s', s) in log-space
                                value = prevAlpha + logCliqueValue;
                            } else {
                                value = Double.NEGATIVE_INFINITY;
                            }

                            // Store values of z_i and max(z_i)
                            values[valueIndex++] = value;
                            if (value > maxValue) maxValue = value;
                        }


                        // compute log(α)
                        logAlpha = logSumExp(values, maxValue);
                    } else {
                        //System.out.println("X -> " + prevNode1.getNode().getWord() + " " + latticeNode.getNode().getWord() + " = -∞");
                        logAlpha = Double.NEGATIVE_INFINITY;
                    }
                    //System.out.println("log α_" + (j + 1) + "(" + prevNode1.getNode().getWord() + ", " + latticeNode.getNode().getWord() + " | x)=" + logAlpha);
                    //System.out.println("------------------------------");
                    latticeNode.setAlpha(prevNode1, logAlpha);
                }
            }

            prevCandidateList2 = prevCandidateList1;
            prevCandidateList1 = currentCandidateList;
            j++;
            //System.out.println("\n==============================");
        }

        return stopLatticeNode2.getAlpha(stopLatticeNode1);
    }

    protected double computeBetas(INode[] output) {
        ListIterator<ITrigramLatticeNodeList> candidateListIterator = this.reverseIterator();
        ITrigramLatticeNodeList nextCandidateList2 = candidateListIterator.previous();
        ITrigramLatticeNodeList nextCandidateList1 = candidateListIterator.previous();

        // fill the lattice with values of β for j = |T|-1 ... 0
        int j = this.mLength;
        while (candidateListIterator.hasPrevious()) {
            ITrigramLatticeNodeList currentCandidateList = candidateListIterator.previous();
            for (ITrigramLatticeNode latticeNode : currentCandidateList) {
                // checks if current node is the labeled output.
                boolean currentOutputMatches = output == null || latticeNode.getNode().getWordId() ==
                        (j < 1 ? Token.START_NODE : (j > output.length ? Token.STOP_NODE : output[j-1])).getWordId();

                for (ITrigramLatticeNode nextNode1: nextCandidateList1) {
                    // checks if current and previous node 1 are the labeled outputs.
                    boolean outputMatches = output == null || (currentOutputMatches && nextNode1.getNode().getWordId() ==
                            (j < 0 ? Token.START_NODE : (j >= output.length ? Token.STOP_NODE : output[j])).getWordId()
                    );

                    double logBeta;
                    if (outputMatches) {
                        // z_i and max(z_i)
                        double[] values = new double[nextCandidateList2.size()];
                        int valueIndex = 0;
                        double maxValue = Double.NEGATIVE_INFINITY;

                        for (ITrigramLatticeNode nextNode2 : nextCandidateList2) {
                            //System.out.println(latticeNode.getNode().getWord() + " " + nextNode1.getNode().getWord() + " <- " + nextNode2.getNode().getWord()+
                            //        "( +log β_" + (j+2) + "(" + nextNode1.getNode().getWord() + ", " + nextNode2.getNode().getWord() + ") )");

                            // β_{j+1}(s' | s", x)
                            double nextBeta = nextNode2.getBeta(nextNode1);

                            double value;
                            if (nextBeta != Double.NEGATIVE_INFINITY) {
                                // Ψ_j(x, s', s)
                                double logCliqueValue = mTransducer.getLogCliqueValue(j+1, mExtras, mInput, latticeNode, nextNode1, nextNode2);

                                // compute β_{j+1}(s' | s", x) * Ψ_j(x, s, s', s") in log-space
                                value = nextBeta + logCliqueValue;
                            } else {
                                value = Double.NEGATIVE_INFINITY;
                            }

                            // Store values of z_i and max(z_i)
                            values[valueIndex++] = value;
                            if (value > maxValue) maxValue = value;
                        }

                        // compute log(β)
                        logBeta = logSumExp(values, maxValue);
                    } else {
                        //System.out.println(latticeNode.getNode().getWord() + " " + nextNode1.getNode().getWord() + " <- X = -∞");
                        logBeta = Double.NEGATIVE_INFINITY;
                    }
                    //System.out.println("log β_" + (j+1) + "(" + latticeNode.getNode().getWord() + ", " + nextNode1.getNode().getWord() + " | x)=" + logBeta);
                    //System.out.println("------------------------------");
                    nextNode1.setBeta(latticeNode, logBeta);
                }
            }

            nextCandidateList2 = nextCandidateList1;
            nextCandidateList1 = currentCandidateList;
            j--;
            //System.out.println("\n==============================");
        }

        return startLatticeNode1.getBeta(startLatticeNode2);
    }

    //</editor-fold>

    //<editor-fold desc="[Viterbi]">

    protected void computeDeltaPsi() {
        Iterator<ITrigramLatticeNodeList> candidateListIterator = this.iterator();
        ITrigramLatticeNodeList prevCandidateList2 = candidateListIterator.next();
        ITrigramLatticeNodeList prevCandidateList1 = candidateListIterator.next();

        // fill the lattice with values of δ for j = 0 ... |T|
        int j = 0;
        while (candidateListIterator.hasNext()) {
            ITrigramLatticeNodeList currentCandidateList = candidateListIterator.next();
            for (ITrigramLatticeNode latticeNode : currentCandidateList) {
                for (ITrigramLatticeNode prevNode1 : prevCandidateList1) {

                    double logDelta = Double.NEGATIVE_INFINITY; // invalid value
                    ITrigramLatticeNode prevPsiNode = null;

                    for (ITrigramLatticeNode prevNode2 : prevCandidateList2) {
                        //System.out.println(prevNode2.getNode().getWord() + " -> " + prevNode1.getNode().getWord() + "  " + latticeNode.getNode().getWord());

                        // δ_{j-1}(s' | s", x)
                        double prevDelta = prevNode1.getDelta(prevNode2);

                        if (prevDelta > Double.NEGATIVE_INFINITY) {
                            // Ψ_j(x, s', s)
                            double logCliqueValue = mTransducer.getLogCliqueValue(j, mExtras, mInput, prevNode2, prevNode1, latticeNode);

                            // compute delta_{j-1} (s' | s", x) * Ψ_j(x, s", s', s) in log-space
                            double logNodeDelta = prevDelta + logCliqueValue;

                            if (prevPsiNode == null || logNodeDelta > logDelta) {
                                logDelta = logNodeDelta;
                                prevPsiNode = prevNode2;
                            }
                        }
                    }

                    //System.out.println("log δ_" + (j + 1) + "(" + prevNode1.getNode().getWord() + ", " + latticeNode.getNode().getWord() + "| x)=" + logDelta);
                    latticeNode.setDelta(prevNode1, logDelta);
                    latticeNode.setPrevPsiNode(prevNode1, prevPsiNode);
                }
            }

            prevCandidateList2 = prevCandidateList1;
            prevCandidateList1 = currentCandidateList;
            j++;
        }
    }

    public INode[] getOptimumLabelSequence() {
        ITrigramLatticeNode optimumDeltaNode2 = stopLatticeNode1,
                optimumDeltaNode1 = stopLatticeNode2.getPrevPsiNode(stopLatticeNode1);

        if (optimumDeltaNode1 == null) return null;

        int pos = mLength - 1;
        INode[] optimumLabels = new INode[mLength];

        while (pos >= 0 && optimumDeltaNode1 != null) {
            optimumLabels[pos--] = optimumDeltaNode1.getNode();

            ITrigramLatticeNode temp = optimumDeltaNode1;
            optimumDeltaNode1 = optimumDeltaNode2.getPrevPsiNode(optimumDeltaNode1);
            optimumDeltaNode2 = temp;
        }

        return optimumLabels;
    }

    //</editor-fold>

    //<editor-fold desc="[Inference]">

    public DoubleVector computeFeaturesEmpiricalValue() {
        assert mOutput != null;
        int nFeatures = mTransducer.getFeatureCount();
        double[] empirical_f = new double[nFeatures];

        ILatticeNode prevNode2 = startLatticeNode2;
        ILatticeNode prevNode1 = startLatticeNode1;
        for (int j = 0; j < mLength + 2; j++) {
            ILatticeNode node = ( j == mLength ? stopLatticeNode1 :
                    (j == mLength + 1 ? stopLatticeNode2 :  mTransducer.buildLatticeNode(mOutput[j])));
            for (int i = 0; i < nFeatures; i++) {
                double f_i = mTransducer.getFeatureValue(i, prevNode2, prevNode1, node, mInput, mExtras, j);

                empirical_f[i] += f_i;
            }

            prevNode2 = prevNode1;
            prevNode1 = node;
        }

        // Normalize e(f_i) values
        DoubleVector list_empirical_f = new DoubleVector(nFeatures);
        for (int i = 0; i < nFeatures; i++) {
            list_empirical_f.set(i, empirical_f[i]);
        }

        return list_empirical_f;
    }

    public DoubleVector computeFeaturesEstimatedValue() {
        assert (!mIsLabeled);
        int nFeatures = mTransducer.getFeatureCount();
        double[] estimated_f = new double[nFeatures];
        double log_Z_lambda = computeAlphaBetaUnlabeled();

        // compute value for the lattice for j = 1 ... |T|
        Iterator<ITrigramLatticeNodeList> candidateListIterator = this.iterator();
        ITrigramLatticeNodeList prevCandidateList2 = candidateListIterator.next();
        ITrigramLatticeNodeList prevCandidateList1 = candidateListIterator.next();

        int j = 0;
        while (candidateListIterator.hasNext()) {
            ITrigramLatticeNodeList currentCandidateList = candidateListIterator.next();

            for (ITrigramLatticeNode prevNode2 : prevCandidateList2) { // for values of s
                for (ITrigramLatticeNode prevNode1 : prevCandidateList1) { // for values of s'
                    for (ITrigramLatticeNode node : currentCandidateList) { // for values of s"
                        //System.out.println(prevNode2.getNode().getWord() + " -> " + prevNode1.getNode().getWord() + " " + node.getNode().getWord());

                        for (int i = 0; i < nFeatures; i++) {
                            // f_i(s,s',s",x,j)
                            double f_i = mTransducer.getFeatureValue(i, prevNode2, prevNode1, node, mInput, mExtras, j);

                            if (f_i > 0.0) {
                                // log α_{j-1}(s, s' | x)
                                double log_alpha_j_1 = prevNode1.getAlpha(prevNode2);

                                // log Ψ_j(x, s, s', s")
                                double logCliqueValue = mTransducer.getLogCliqueValue(j, mExtras, mInput, prevNode2, prevNode1, node);

                                // log β_{j}(s', s" | x)
                                double log_beta_j = node.getBeta(prevNode1);

                                // Computing ( f_i(s,s',s",x,j) * α_{j-1}(s, s' | x) * Ψ_j(x, s, s', s'') * β_{j}(s', s'' | x) ) / Z_λ
                                estimated_f[i] += f_i * Math.exp(log_alpha_j_1 + logCliqueValue + log_beta_j - log_Z_lambda);
                            }
                        }
                    }
                }
            }

            prevCandidateList2 = prevCandidateList1;
            prevCandidateList1 = currentCandidateList;
            j++;
        }

        // Normalize e~(f_i) values
        DoubleVector list_estimated_f = new DoubleVector(nFeatures);
        for (int i = 0; i < nFeatures; i++) {
            list_estimated_f.set(i, estimated_f[i]);
        }

        return list_estimated_f;
    }

    //</editor-fold>
}
