package de.iisys.ocr.optimizer;

/**
 * IFunction
 * de.iisys.ocr.optimizer
 * Created by reza on 15.09.14.
 */
public interface IFunction {
    int getNumParams();
    double getValue();
    double getValueConfidence();
    void getValueGradient(double[] gradient);
    void getParameters(double[] params);
    void setParameters(double[] params);
}
