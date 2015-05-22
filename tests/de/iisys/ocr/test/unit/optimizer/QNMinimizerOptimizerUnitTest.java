package de.iisys.ocr.test.unit.optimizer;

import de.iisys.ocr.optimizer.IFunction;
import de.iisys.ocr.optimizer.IOptimizer;
import de.iisys.ocr.optimizer.QNMinimizerOptimizer;
import de.iisys.ocr.test.core.BaseTest;
import org.junit.Test;

/**
 * QNMinimizerOptimizerUnitTest
 * Created by reza on 25.01.15.
 */
public class QNMinimizerOptimizerUnitTest extends BaseTest {

    @Test
    public void testOptimize() {
        final int m = 2;
        final double[] vars = new double[] { 0.0 };
        IFunction mFunction1D = new BaseTest.Simple1DPoly(vars, -1);

        IOptimizer optimizer = new QNMinimizerOptimizer(mFunction1D);
        optimizer.optimize();
        double extremumValue = mFunction1D.getValue();

        assert Math.abs(extremumValue + 3.083333333333333) < 1E-5;
        assert Math.abs(vars[0] - 0.83333333333333) < 1E-5;
    }

    @Test
    public void testOptimizer2D() {
        final double[] vars = new double[] { 0.0, 0.0 };
        IFunction function2D = new Simple2DPoly(vars, -1);

        IOptimizer optimizer = new QNMinimizerOptimizer(function2D);
        optimizer.optimize();
        double extremumValue = function2D.getValue();

        assert Math.abs(extremumValue + 5.33333333333333) < 1E-5;
        assert Math.abs(vars[0] - 0.83333333333333) < 1E-5;
        assert Math.abs(vars[1] - 1.5) < 1E-5;
    }
}
