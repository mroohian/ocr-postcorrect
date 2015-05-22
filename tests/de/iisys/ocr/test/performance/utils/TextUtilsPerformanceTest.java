package de.iisys.ocr.test.performance.utils;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.utils.TextUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * TextUtilsPerformanceTest
 * de.iisys.ocr.test.performance.utils
 * Created by reza on 19.08.14.
 */
public class TextUtilsPerformanceTest extends BaseTest {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @Test
    @BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 10)
    public void testFormatColumnStringPerformance() {
        for (int i = 0; i < 1000000; i++) {
            String result = TextUtils.formatColumnString("test", 15);
            assert result.equals("test           \t");
        }
    }
}
