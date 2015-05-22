package de.iisys.ocr.optimizer;

/**
 * MatrixOpt
 * de.iisys.ocr.optimizer
 * Ported by reza on 18.09.14.
 */
public class MatrixOps {

    public static void setAll(double[] m, double v) {
        java.util.Arrays.fill (m, v);
    }

    public static double oneNorm (double[] m) {
        double ret = 0;
        for (int i = 0; i < m.length; i++)
            ret += m[i];
        return ret;
    }

    public static double twoNorm(double[] m) {
        double ret = 0;
        for (int i = 0; i < m.length; i++)
            ret += m[i] * m[i];
        return Math.sqrt (ret);
    }

    public static double infinityNorm (double[] m) {
        double ret = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < m.length; i++)
            if (Math.abs(m[i]) > ret)
                ret = Math.abs(m[i]);
        return ret;
    }

    public static void timesEquals (double[] m, double factor) {
        for (int i=0; i < m.length; i++)
            m[i] *= factor;
    }

    public static void plusEquals (double[] m, double toadd) {
        for (int i=0; i < m.length; i++)
            m[i] += toadd;
    }

    public static void plusEquals (double[] m1, double[] m2) {
        assert (m1.length == m2.length) : "unequal lengths\n";
        for (int i=0; i < m1.length; i++) {
            if (Double.isInfinite(m1[i]) && Double.isInfinite(m2[i]) && (m1[i]*m2[i] < 0))
                m1[i] = 0.0;
            else
                m1[i] += m2[i];
        }
    }

    public static void plusEquals (double[] m1, double[] m2,  double factor) {
        assert (m1.length == m2.length) : "unequal lengths\n";
        for (int i=0; i < m1.length; i++) {
            double m1i = m1[i];
            double m2i = m2[i];
            if (Double.isInfinite(m1i) && Double.isInfinite(m2i) && (m1[i]*m2[i] < 0))
                m1[i] = 0.0;
            else  m1[i] += m2[i] * factor;
        }
    }

    public static void plusEquals (double[][] m1, double[][] m2,  double factor)
    {
        assert (m1.length == m2.length) : "unequal lengths\n";
        for (int i=0; i < m1.length; i++) {
            for (int j=0; j < m1[i].length; j++) {
                m1[i][j] += m2[i][j] * factor;
            }
        }
    }

    public static double dotProduct (double[] m1, double[] m2) {
        assert (m1.length == m2.length) : "m1.length != m2.length\n";
        double ret = 0.0;
        for (int i=0; i < m1.length; i++)
            ret += m1[i] * m2[i];
        return ret;
    }

    public static double absNorm (double[] m) {
        double ret = 0;
        for (int i = 0; i < m.length; i++)
            ret += Math.abs(m[i]);
        return ret;
    }

    public static double absNormalize (double[] m) {
        double norm = absNorm(m);
        if (norm > 0)
            for (int i = 0; i < m.length; i++)
                m[i] /= norm;
        return norm;
    }

    public static boolean isNaN(double[] m) {
        for (int i = 0; i < m.length; i++)
            if (Double.isNaN(m[i]))
                return true;
        return false;
    }
}
