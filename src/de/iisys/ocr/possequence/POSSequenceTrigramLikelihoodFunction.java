package de.iisys.ocr.possequence;

import de.iisys.ocr.optimizer.IFunction;
import de.iisys.ocr.possequence.lattice.POSSequenceTrigramLattice;

import java.util.List;

/**
 * POSSequenceTrigramLikelihoodFunction
 * Created by reza on 26.01.15.
 */
public class POSSequenceTrigramLikelihoodFunction implements IFunction {
    private POSSequenceTrigramTransducer transducer;
    private int iter = 0;
    private double functionValue;
    private double log_Z_lambda;
    private boolean changed = true;
    private final List<POSSequenceTrigramLattice> latticeInstances;
    private final double[] derivatives;

    public POSSequenceTrigramLikelihoodFunction(POSSequenceTrigramTransducer transducer, List<POSSequenceTrigramLattice> latticeInstances) {
        this.transducer = transducer;
        this.latticeInstances = latticeInstances;
        derivatives = new double[getNumParams()];
    }

    public void compute() {
        changed = false;

        // Clear
        functionValue = 0.0;
        for (int i = 0; i < getNumParams(); i++) {
            derivatives[i] = 0;
        }

        for (POSSequenceTrigramLattice lattice : latticeInstances) { // x
            lattice.clearLattice();
            /* Print lattice */
            //lattice.printLattice(corpa);

            /* Compute derivatives */
            double log_Z_lambda = lattice.updateFeatureDerivatives(derivatives);

            /* Compute function value */
            functionValue += lattice.computeOutputSequenceProbability(log_Z_lambda);
        }

        // penalized derivatives
        for (int i = 0; i<getNumParams(); i++) {
            derivatives[i] -= transducer.getFeatureWeights(i) /* / sigma2 */;
        }

        // penalize function value
        for (int i = 0; i<getNumParams(); i++) {
            functionValue -= (
                (transducer.getFeatureWeights(i) * transducer.getFeatureWeights(i)
            ) / 2/* * sigma2 */);
        }

        /* Optimization step */
        System.out.println("F: " + functionValue);
        System.out.println(String.format("Prob: %.2f %%", Math.exp(functionValue) * 100.0));
        System.out.println("G: " + derivatives[0] + ", " + derivatives[1]);
    }

    @Override
    public int getNumParams() {
        return transducer.getFeatureCount();
    }

    @Override
    public double getValue() {
        if (changed) compute();
        return -functionValue;
    }

    @Override
    public double getValueConfidence() {
        if (changed) compute();
        return Math.exp(functionValue);
    }

    @Override
    public void getValueGradient(double[] gradient) {
        if (changed) compute();
        for (int i = 0; i < getNumParams(); i++) {
            gradient[i] = -derivatives[i];
        }
    }

    @Override
    public void getParameters(double[] params) {
        for (int i = 0; i < getNumParams(); i++) {
            params[i] = transducer.getFeatureWeights(i);
        }
    }

    @Override
    public void setParameters(double[] params) {
        for (int i = 0; i < getNumParams(); i++) {
            if (transducer.getFeatureWeights(i) != params[i]) {
                changed = true;
                break;
            }
        }
        if (changed == false) return;

        for (int i = 0; i < getNumParams(); i++) {
            transducer.setFeatureWeights(i, params[i]);
            System.out.println("lambda_" + i + ": " + params[i]);
        }
    }
}
