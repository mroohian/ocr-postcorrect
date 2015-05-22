package de.iisys.ocr.transducer.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.transducer.ITransducer;
import de.iisys.ocr.types.DoubleVector;

/**
 * Lattice
 * de.iisys.ocr.transducer.lattice
 * Created by reza on 28.07.14.
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
public abstract class Lattice {
    protected final String[] mInput;
    protected final Object[] mExtras;
    protected INode[] mOutput;
    protected final int mLength;
    protected final ILatticeNodeList[] latticeNodes;

    // forward-backward normalization constant
    private double mZ_lambda;
    private double mZ_lambdaBackward;

    private boolean mZ_lambdaComputed;
    protected boolean mIsLabeled;

    public Lattice(String[] input, String[] extras) {
        mLength = input.length;
        mInput = input;
        mExtras = extras;
        mOutput = null;
        latticeNodes = new ILatticeNodeList[mLength];
    }

    public Lattice(String[] input, INode[] output) {
        mLength = input.length;
        mInput = input;
        mExtras = null;
        mOutput = output;
        latticeNodes = new ILatticeNodeList[mLength];
    }

    public Lattice(String[] input, Object[] extras, INode[] output) {
        mLength = input.length;
        mInput = input;
        mExtras = extras;
        mOutput = output;
        latticeNodes = new ILatticeNodeList[mLength];
    }

    protected void init() {
        reset();
        fillLattice();
    }

    protected abstract void fillLattice();

    protected abstract void resetLatticeNodes();

    public void reset() {
        mZ_lambdaComputed = false;
        mIsLabeled = true; // Force unlabeled computation

        mZ_lambda = 0.0;
        mZ_lambdaBackward = 0.0;

        resetLatticeNodes();
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

    //<editor-fold desc="[Getter-Setter]">

    public abstract ITransducer getTransducer();

    public INode[] getOutput() {
        return mOutput;
    }

    public void setOutput(INode[] output) {
        mOutput = output;
    }

    /*public int getModelFeatureCount() {
        return getTransducer().getFeatureCount();
    }

    public DoubleVector getModelParams() {
        return getTransducer().getParamsVector();
    }*/

    //</editor-fold>

    //<editor-fold desc="[Alpha-Beta]">

    protected abstract double computeAlphas(INode[] output);

    protected abstract double computeBetas(INode[] output);

    private double computeAlphasLabeled() {
        return computeAlphas(mOutput);
    }

    private double computeBetasLabeled() {
        return computeBetas(mOutput);
    }

    private double computeAlphasUnlabeled() {
        return computeAlphas(null);
    }

    private double computeBetasUnlabeled() {
        return computeBetas(null);
    }

    public double computeAlphaBetaLabeled() {
        double z_lambda = computeAlphasLabeled();
        double z_lambdaBackward = computeBetasLabeled();
        assert !Double.isInfinite(z_lambda);
        assert !Double.isInfinite(z_lambdaBackward);
        assert Math.abs(z_lambda - z_lambdaBackward) < 1E-12;
        mIsLabeled = true;
        return z_lambda;
    }

    public double computeAlphaBetaUnlabeled() {
        if (mIsLabeled || !mZ_lambdaComputed) {
            mZ_lambda = computeAlphasUnlabeled();
            mZ_lambdaBackward = computeBetasUnlabeled();
            assert Math.abs(mZ_lambda - mZ_lambdaBackward) < 1E-12;
            mZ_lambdaComputed = true;
            mIsLabeled = false;
        }
        return mZ_lambda;
    }

    //</editor-fold>

    //<editor-fold desc="[Viterbi]">

    protected abstract void computeDeltaPsi();

    public void computeViterbi() {
        computeDeltaPsi();
    }

    public abstract INode[] getOptimumLabelSequence();

    //</editor-fold>

    //<editor-fold desc="[Inference]">

    public abstract DoubleVector computeFeaturesEmpiricalValue();

    public abstract DoubleVector computeFeaturesEstimatedValue();

    public double computeLikelihood() {
        // NOTE: The order here matters because the unlabeled computation creates the correct alpha-beta table
        // which is required in the calculation of gradient.
        double likelihood = computeAlphaBetaLabeled();
        double z_lambda = computeAlphaBetaUnlabeled();
        assert (!mIsLabeled);
        // log ( L / Z)
        return likelihood - z_lambda;
    }

    //</editor-fold>
}
