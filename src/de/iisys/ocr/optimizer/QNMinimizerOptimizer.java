package de.iisys.ocr.optimizer;

import edu.stanford.nlp.optimization.DiffFunction;
import edu.stanford.nlp.optimization.QNMinimizer;

/**
 * QNMinimizerOptimizer
* Created by reza on 25.01.15.
*/
public class QNMinimizerOptimizer implements IOptimizer {
    private final QNMinimizer qn;
    private final int m;
    private final double[] params;
    private final double[] gradients;
    private final DiffFunction dfunction;
    private IFunction function;

    public QNMinimizerOptimizer(final IFunction function) {
        this.function = function;
        this.m = function.getNumParams();
        this.params = new double[m];
        this.gradients = new double[m];
        this.qn = new QNMinimizer();

        this.dfunction = new DiffFunction() {
            @Override
            public double[] derivativeAt(double[] doubles) {
                function.setParameters(doubles);
                function.getValueGradient(gradients);
                //for (int i = 0; i < m; i++) gradients[i] = -gradients[i];
                return gradients;
            }

            @Override
            public double valueAt(double[] doubles) {
                function.setParameters(doubles);
                return function.getValue();
            }

            @Override
            public int domainDimension() {
                return m;
            }
        };
    }

    @Override
    public boolean optimize() {
        double[] result = qn.minimize(dfunction, 1E-8, params);
        function.setParameters(result);
        return false;
    }

    @Override
    public boolean optimize(int numIterations) {
        double[] result = qn.minimize(dfunction, 1E-6, params, numIterations);
        function.setParameters(result);
        return false;
    }

    @Override
    public boolean isConverged() {
        return qn.wasSuccessful();
    }

    @Override
    public IFunction getOptimizable() {
        return function;
    }
}
