package de.iisys.ocr.test.performance.tokenizer;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.tokenizer.ITokenizer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * DelimiterTokenizerPerformanceTest
 * de.iisys.ocr.test.performance.tokenizer
 * Created by reza on 16.09.14.
 */
public class DelimiterTokenizerPerformanceTest extends BaseTest {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @Test
    @BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 10)
    public void testTokenizerPerformance() {
        final String content = StringUtils.repeat(mSampleTokenizerText, 5000);

        ITokenizer tokenizer = new DelimiterTokenizer(content);

        while (tokenizer.hasNext()) tokenizer.next();
    }
}
