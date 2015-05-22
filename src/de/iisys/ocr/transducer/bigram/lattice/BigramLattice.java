package de.iisys.ocr.transducer.bigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.transducer.ITransducer;
import de.iisys.ocr.transducer.bigram.IBigramTransducer;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.lattice.Lattice;
import de.iisys.ocr.types.DoubleVector;
import de.iisys.ocr.types.Token;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * BigramLattice
 * de.iisys.ocr.transducer.bigram.lattice
 * Created by reza on 15.10.14.
 */
public abstract class BigramLattice extends Lattice implements IBigramLattice {
    private final IBigramTransducer mTransducer;

    private final IBigramLatticeNode startLatticeNode = new BigramLatticeNode(Token.START_NODE, true) {
        public String getDetails() { return "⊥"; }
    };
    private final IBigramLatticeNode stopLatticeNode = new BigramLatticeNode(Token.STOP_NODE, true) {
        public String getDetails() { return "⊤"; }
    };
    private final IBigramLatticeNodeList startList = new BigramLatticeNodeList(-1, 1, startLatticeNode);
    private final IBigramLatticeNodeList stopList = new BigramLatticeNodeList(-1, 1, stopLatticeNode);

    @Override
    protected void resetLatticeNodes() {
        if (stopLatticeNode != null) {
            stopLatticeNode.setPrevPsiNode(null);
            stopLatticeNode.setDelta(0.0);
        }
    }

    public BigramLattice(IBigramTransducer transducer, String[] input, INode[] output) {
        super(input, output);

        mTransducer = transducer;
        init();
    }

    //<editor-fold desc="[Iterable]">

    public Iterator<IBigramLatticeNodeList> getIterator(boolean reverse) {
        final int length = latticeNodes.length;
        final int preceding = 1;
        final int ending = 1;
        final int initPos = (reverse ? length - 1 + ending : - preceding);

        return new ListIterator<IBigramLatticeNodeList>() {
            private int pos = initPos;

            @Override
            public boolean hasNext() {
                return pos < length + ending;
            }

            @Override
            public boolean hasPrevious() {
                return pos >= -preceding;
            }

            private IBigramLatticeNodeList getListAtPos(int pos) {
                final IBigramLatticeNodeList nodeArray;

                if (pos < 0) {
                    nodeArray = startList;
                } else if (pos >= length) {
                    nodeArray = stopList;
                } else {
                    nodeArray = (IBigramLatticeNodeList)latticeNodes[pos];
                }

                return nodeArray;
            }

            @Override
            public IBigramLatticeNodeList next() {
                return getListAtPos(pos++);
            }

            @Override
            public IBigramLatticeNodeList previous() {
                return getListAtPos(pos--);
            }

            @Override
            public int nextIndex() { return pos + 1; }

            @Override
            public int previousIndex() { return pos - 1; }

            @Override
            public void set(IBigramLatticeNodeList iLatticeNodes) { }

            @Override
            public void add(IBigramLatticeNodeList iLatticeNodes) { }

            @Override
            public void remove() { }
        };
    }

    @Override
    public Iterator<IBigramLatticeNodeList> iterator() {
        return getIterator(false);
    }

    @Override
    public ListIterator<IBigramLatticeNodeList> forwardIterator() {
        return (ListIterator<IBigramLatticeNodeList>)getIterator(false);
    }

    @Override
    public ListIterator<IBigramLatticeNodeList> reverseIterator() {
        return (ListIterator<IBigramLatticeNodeList>)getIterator(true);
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
        Iterator<IBigramLatticeNodeList> candidateListIterator = this.iterator();
        IBigramLatticeNodeList prevCandidateList = candidateListIterator.next();

        // fill the lattice with values of α for j = 0 ... |T|
        int j = 0;
        while (candidateListIterator.hasNext()) {
            IBigramLatticeNodeList currentCandidateList = candidateListIterator.next();
            for (IBigramLatticeNode latticeNode : currentCandidateList) {
                boolean outputMatches = (output == null || latticeNode.getNode().getWordId() ==
                        (j < 0 ? Token.START_NODE : (j >= output.length ? Token.STOP_NODE : output[j])).getWordId()
                );

                double logAlpha;
                if (outputMatches) {
                    // z_i and max(z_i)
                    double[] values = new double[prevCandidateList.size()];
                    int valueIndex = 0;
                    double maxValue = Double.NEGATIVE_INFINITY;

                    for (IBigramLatticeNode prevNode : prevCandidateList) {
                        //System.out.println(prevNode.getNode().getWord() + " -> " + latticeNode.getNode().getWord());

                        // α_{j-1}(s' | x)
                        double prevAlpha = prevNode.getAlpha();

                        double value;
                        if (prevAlpha != Double.NEGATIVE_INFINITY) {
                            // Ψ_j(x, s', s)
                            double logCliqueValue = mTransducer.getLogCliqueValue(j, mExtras, mInput, prevNode, latticeNode);

                            // compute α_{j-1} (s' | x) * Ψ_j(x, s', s) in log-space
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
                    //System.out.println("X -> " + latticeNode.getNode().getWord() + " = -∞");
                    logAlpha = Double.NEGATIVE_INFINITY;
                }
                //System.out.println("log α_" + (j+1) + "(" + latticeNode.getNode().getWord() + "|x)=" + logAlpha);
                latticeNode.setAlpha(logAlpha);
            }

            prevCandidateList = currentCandidateList;
            j++;
        }

        return stopLatticeNode.getAlpha();
    }

    protected double computeBetas(INode[] output) {
        ListIterator<IBigramLatticeNodeList> candidateListIterator = this.reverseIterator();
        IBigramLatticeNodeList candidateListNext = candidateListIterator.previous();

        // fill the lattice with values of β for j = |T|-1 ... 0
        int j = this.mLength;
        while (candidateListIterator.hasPrevious()) {
            IBigramLatticeNodeList currentCandidateList = candidateListIterator.previous();
            for (IBigramLatticeNode latticeNode : currentCandidateList) {
                boolean outputMatches = (output == null || latticeNode.getNode().getWordId() ==
                        (j < 1 ? Token.START_NODE : (j > output.length ? Token.STOP_NODE : output[j-1])).getWordId()
                );

                double logBeta;
                if (outputMatches) {
                    // z_i and max(z_i)
                    double[] values = new double[candidateListNext.size()];
                    int valueIndex = 0;
                    double maxValue = Double.NEGATIVE_INFINITY;

                    for (IBigramLatticeNode nextNode: candidateListNext) {
                        //System.out.println(latticeNode.getNode().getWord() + " <- " + nextNode.getNode().getWord());

                        // β_{j+1}(s' | x)
                        double nextBeta = nextNode.getBeta();

                        double value;
                        if (nextBeta != Double.NEGATIVE_INFINITY) {
                            // Ψ_j(x, s', s)
                            double logCliqueValue = mTransducer.getLogCliqueValue(j, mExtras, mInput, latticeNode, nextNode);

                            // compute β_{j+1}(s' | x) * Ψ_j(x, s, s') in log-space
                            value = nextBeta + logCliqueValue;
                        } else {
                            value = Double.NEGATIVE_INFINITY;
                        }

                        // Store values of z_i and max(z_i)
                        values[valueIndex++] = value;
                        if (value > maxValue) maxValue = value;
                    }

                    // compute log(α)
                    logBeta = logSumExp(values, maxValue);
                } else {
                    //System.out.println(latticeNode.getNode().getWord() + " <- X = -∞");
                    logBeta = Double.NEGATIVE_INFINITY;
                }
                //System.out.println("log β_" + (j+1) + "(" + latticeNode.getNode().getWord() + "|x)=" + logBeta);
                latticeNode.setBeta(logBeta);
            }

            candidateListNext = currentCandidateList;
            j--;
        }

        return startLatticeNode.getBeta();
    }

    //</editor-fold>

    //<editor-fold desc="[Viterbi]">

    protected void computeDeltaPsi() {
        Iterator<IBigramLatticeNodeList> candidateListIterator = this.iterator();
        IBigramLatticeNodeList prevCandidateList = candidateListIterator.next();

        // fill the lattice with values of δ for j = 0 ... |T|
        int j = 0;
        while (candidateListIterator.hasNext()) {
            IBigramLatticeNodeList currentCandidateList = candidateListIterator.next();
            for (IBigramLatticeNode latticeNode : currentCandidateList) {
                double logDelta = Double.NEGATIVE_INFINITY; // invalid value
                IBigramLatticeNode prevPsiNode = null;

                for (IBigramLatticeNode prevNode : prevCandidateList) {
                    //System.out.println(prevNode.getNode().getWord() + " -> " + latticeNode.getNode().getWord());

                    // δ_{j-1}(s' | x)
                    double prevDelta = prevNode.getDelta();

                    if (prevDelta > Double.NEGATIVE_INFINITY) {
                        // Ψ_j(x, s', s)
                        double logCliqueValue = mTransducer.getLogCliqueValue(j, mExtras, mInput, prevNode, latticeNode);

                        // compute delta_{j-1} (s' | x) * Ψ_j(x, s', s) in log-space
                        double logNodeDelta = prevDelta + logCliqueValue;

                        if (prevPsiNode == null || logNodeDelta > logDelta) {
                            logDelta = logNodeDelta;
                            prevPsiNode = prevNode;
                        }
                    }
                }

                //System.out.println("log δ_" + (j + 1) + "(" + latticeNode.getNode().getWord() + "|x)=" + logDelta);
                latticeNode.setDelta(logDelta);
                latticeNode.setPrevPsiNode(prevPsiNode);
            }

            prevCandidateList = currentCandidateList;
            j++;
        }
    }

    public INode[] getOptimumLabelSequence() {
        IBigramLatticeNode optimumDeltaNode = stopLatticeNode.getPrevPsiNode();

        if (optimumDeltaNode == null) return null;

        int pos = mLength - 1;
        INode[] optimumLabels = new INode[mLength];

        while (pos >= 0 && optimumDeltaNode != null) {
            optimumLabels[pos--] = optimumDeltaNode.getNode();
            optimumDeltaNode = optimumDeltaNode.getPrevPsiNode();
        }

        return optimumLabels;
    }

    //</editor-fold>

    //<editor-fold desc="[Inference]">

    public DoubleVector computeFeaturesEmpiricalValue() {
        assert mOutput != null;
        int nFeatures = mTransducer.getFeatureCount();
        double[] empirical_f = new double[nFeatures];

        ILatticeNode prevNode = startLatticeNode;
        for (int j = 0; j <= mLength; j++) {
            ILatticeNode node = ( j == mLength ? stopLatticeNode : mTransducer.buildLatticeNode(mOutput[j]));
            for (int i = 0; i < nFeatures; i++) {
                double f_i = mTransducer.getFeatureValue(i, prevNode, node, mInput, mExtras, j);

                empirical_f[i] += f_i;
            }

            prevNode = node;
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
        Iterator<IBigramLatticeNodeList> candidateListIterator = this.iterator();
        IBigramLatticeNodeList prevCandidateList = candidateListIterator.next();
        int j = 0;
        while (candidateListIterator.hasNext()) {
            IBigramLatticeNodeList currentCandidateList = candidateListIterator.next();

            for (IBigramLatticeNode prevNode : prevCandidateList) { // for values of s
                for (IBigramLatticeNode latticeNode : currentCandidateList) { // for values of s'
                    //System.out.println(prevNode.getNode().getWord() + " -> " + latticeNode.getNode().getWord());

                    for (int i = 0; i < nFeatures; i++) {
                        // f_i(s,s',x,j)
                        double f_i = mTransducer.getFeatureValue(i, prevNode, latticeNode, mInput, mExtras, j);

                        if (f_i > 0.0) {
                            // log α_{j}(s | x)
                            double log_alpha_j = prevNode.getAlpha();

                            // log Ψ_j(x, s, s')
                            double logCliqueValue = mTransducer.getLogCliqueValue(j, mExtras, mInput, prevNode, latticeNode);

                            // log β_{j+1}(s' | x)
                            double log_beta_j_1 = latticeNode.getBeta();

                            // Computing ( f_i(s,s',x,j) * α_{j}(s | x) * Ψ_j(x, s, s') * β_{j+1}(s' | x) ) / Z_λ
                            estimated_f[i] += f_i * Math.exp(log_alpha_j + logCliqueValue + log_beta_j_1 - log_Z_lambda);
                        }
                    }

                }
            }

            prevCandidateList = currentCandidateList;
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
