package de.iisys.ocr.optimizer;

/**
 * de.iisys.ocr.optimizer
 * Created by reza on 18.09.14.
 */
public interface ILineOptimizer {
    double optimize (double[] line, double initialStep);
}
