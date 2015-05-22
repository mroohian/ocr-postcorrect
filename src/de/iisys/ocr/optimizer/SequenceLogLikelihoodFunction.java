package de.iisys.ocr.optimizer;

import de.iisys.ocr.transducer.ITransducer;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.Lattice;
import de.iisys.ocr.types.DoubleVector;

import java.util.List;

/**
 * SequenceLogLikelihoodFunction
 * de.iisys.ocr.optimizer
 * Created by reza on 18.09.14.
 */
public class SequenceLogLikelihoodFunction implements IFunction {
    private final double sigma_2 = 1.0;
    private final double sigma_2_2 = 2 * sigma_2;

    private final ITransducer mTransducer;
    private final DoubleVector mTransducerParamsVector;
    private final List<IFeature> mFeatures;

    private final Lattice[] mLattice;
    private final DoubleVector mEmpiricalValues;
    private final Boolean mPenalized;

    private double mValue = 0.0;
    private double mPenalizedValue = 0.0;

    public SequenceLogLikelihoodFunction(ITransducer transducer, Boolean penalized, Lattice... lattices) {
        mTransducer = transducer;
        mTransducerParamsVector = mTransducer.getParamsVector();
        mFeatures = mTransducer.getFeatures();

        mPenalized = penalized;

        mLattice = new Lattice[lattices.length];
        DoubleVector empiricalValues = new DoubleVector(mTransducerParamsVector.size());
        for (int i = 0; i < lattices.length; i++) {
            mLattice[i] = lattices[i];
            empiricalValues = empiricalValues.sum(mLattice[i].computeFeaturesEmpiricalValue());
        }
        mEmpiricalValues = empiricalValues;
    }

    @Override
    public int getNumParams() { return mTransducerParamsVector.size(); }

    @Override
    public double getValue() {
        double value =  0.0;

        for (int i = 0; i < mLattice.length; i++) value += mLattice[i].computeLikelihood();

        if (mPenalized) {
            // Computing Σ (λ_i^2 / 2 * σ^2)
            double penalized = mTransducerParamsVector.divide(sigma_2_2).vectorSum();
            mPenalizedValue = penalized;
            value -= penalized;
        }

        mValue = value;

        return value;
    }

    @Override
    public double getValueConfidence() {
        return Math.exp(mValue);
    }

    @Override
    public void getValueGradient(double[] gradient) {
        DoubleVector estimatedValues = new DoubleVector(mTransducerParamsVector.size());
        for (int i = 0; i < mLattice.length; i++) estimatedValues = estimatedValues.sum(mLattice[i].computeFeaturesEstimatedValue());

        // Computing (∂L(T) / ∂λ_i) - (λ_i / σ^2)
        DoubleVector vectorLambda;
        if (mPenalized) {
            // Note: (λ_i / σ^2) is penalized amount
            vectorLambda = mEmpiricalValues.subtract(estimatedValues).subtract(mTransducerParamsVector.divide(sigma_2));
        } else {
            vectorLambda = mEmpiricalValues.subtract(estimatedValues);
        }
        double[] src = vectorLambda.toArray();
        System.arraycopy(src, 0, gradient, 0, gradient.length);
    }

    @Override
    public void getParameters(double[] params) {
        double[] src = mTransducerParamsVector.toArray();
        System.arraycopy(src, 0, params, 0, params.length);
    }

    @Override
    public void setParameters(double[] params) {
        mTransducerParamsVector.set(params);

        for (int i = 0; i < mLattice.length; i++) mLattice[i].reset();

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Value: %.4f, Confidence: %.4f%%, ", mValue, getValueConfidence() * 100.0));
        for (int i = 0; i < mFeatures.size(); i++) {
            builder.append(String.format("%s(λ=%.4f)", mFeatures.get(i).getName(), params[i]));
            if (i != mFeatures.size()-1) builder.append(", ");
        }
        System.out.println(builder.toString());
    }
}
