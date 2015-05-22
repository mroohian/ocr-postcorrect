package de.iisys.ocr.test.core;

import de.iisys.ocr.optimizer.LinearFunction;

/**
 * BaseTest
 * de.iisys.ocr.test.core
 * Created by reza on 04.09.14.
 */
public class BaseTest {
    // Corpus file
    protected static final String mCorpusFile =  System.getProperty("user.dir") + "/data/corpa/derewo-v-100000t-2009-04-30-0.1.utf8";

    // POS Corpus files
    protected static final String mPOSAlphabetFile = System.getProperty("user.dir") + "/tmp/generator.pab";
    protected static final String mWordAlphabetFile = System.getProperty("user.dir") + "/tmp/generator.wab";
    protected static final String mWordPOSMappingFile = System.getProperty("user.dir") + "/tmp/generator.wpm";
    protected static final String mPOSTrigramsFile = System.getProperty("user.dir") + "/tmp/generator.ptg";
    protected static final String mMixedTrigramsFile = System.getProperty("user.dir") + "/tmp/generator.mtg";

    // Archive path
    protected static final String mArchivePath = System.getProperty("user.dir") + "/mnt/dump/archive";

    // Empty graph
    protected static final String mEmptyGraphDBPath = "./tmp/test-empty-db";
    protected static final String mEmptyGraphUser = "admin";
    protected static final String mEmptyGraphPassword = "admin";

    // Local graph
    protected static final String mLocalUser = "admin";
    protected static final String mLocalPassword = "admin";
    protected static final String mLocalGraphDBPath = "./db/test-db1";

    // Remote graph
    protected static final String mRemoteUser = "reza";
    protected static final String mRemotePassword = "sec12345";
    protected static final String mRemoteDbName = "complete-de";
    protected static final String mRemoteServer = "172.16.50.110";

    // Tesseract
    protected static final String mTesseractDataPath = System.getProperty("user.dir") + "/Modules/Tess4J/tessdata";

    // Tokenizer
    protected static final String mStanfordPOSTaggerModelPath = System.getProperty("user.dir") + "/data/models";
    protected static final String mSampleTokenizerText = "This is a test. Sample word's letters, \"Quotation\" and punctuations! I'd like to test.";

    // Optimizer
    protected class Simple1DPoly extends LinearFunction {
        private final double multi;

        public Simple1DPoly(double[] vars, double multi) {
            super(vars);
            this.multi = multi;
            assert vars.length == 1;
        }

        @Override
        public double getValue() {
            double x = mVars[0];
            return multi * (-3 * x * x + 5 * x + 1);
        }

        @Override
        public void getValueGradient(double[] gradient) {
            double x = mVars[0];
            gradient[0] = multi * (-6 * x + 5);
        }
    }

    protected class Simple2DPoly extends LinearFunction {
        private final double multi;

        public Simple2DPoly(double[] vars, double multi) {
            super(vars);
            this.multi = multi;
            assert vars.length == 2;
        }

        @Override
        public double getValue() {
            double x = mVars[0];
            double y = mVars[1];
            return multi * (-3 * x * x - y * y + 5 * x + 3 * y + 1);
        }

        @Override
        public void getValueGradient(double[] gradient) {
            double x = mVars[0];
            double y = mVars[1];
            gradient[0] = multi * (-6 * x + 5);
            gradient[1] = multi * (-2 * y + 3);
        }
    }

    protected static class StringUtils {
        public static String repeat(final String input, final int n) {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < n; i++) {
                builder.append(input);
            }

            return builder.toString();
        }
    }
}
