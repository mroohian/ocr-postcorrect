package de.iisys.ocr.optimizer;

/**
 * LinearFunction
 * de.iisys.ocr.optimizer
 * Created by reza on 18.09.14.
 */
public abstract class LinearFunction implements IFunction {
    protected final double[] mVars;

    public LinearFunction(double[] vars) {
        mVars = vars;
    }

    @Override
    public int getNumParams() {
        return mVars.length;
    }

    @Override
    public abstract double getValue();

    @Override
    public abstract void getValueGradient(double[] gradient);

    @Override
    public double getValueConfidence() {
        return 1.0;
    }

    @Override
    public void getParameters(double[] params) {
        assert params.length == mVars.length;
        System.arraycopy(mVars, 0, params, 0, params.length);
    }

    @Override
    public void setParameters(double[] params) {
        assert params.length == mVars.length;
        System.arraycopy(params, 0, mVars, 0, mVars.length);
    }
}
