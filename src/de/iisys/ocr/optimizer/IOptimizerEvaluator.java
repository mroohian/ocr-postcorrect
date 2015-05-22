package de.iisys.ocr.optimizer;

/**
 * de.iisys.ocr.optimizer
 * Created by reza on 18.09.14.
 */
public interface IOptimizerEvaluator {
    boolean evaluate (IFunction maxable, int iter);
}
