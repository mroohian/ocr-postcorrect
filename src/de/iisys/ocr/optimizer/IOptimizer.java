package de.iisys.ocr.optimizer;

/**
 * IOptimizer
 * de.iisys.ocr.optimizer
 * Created by reza on 15.09.14.
 */
public interface IOptimizer {
    public boolean optimize();
    public boolean optimize(int numIterations);
    public boolean isConverged();
    public IFunction getOptimizable();
}
