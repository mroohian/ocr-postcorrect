package de.iisys.ocr.test.performance.tokenizer;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.tokenizer.ITokenizer;
import de.iisys.ocr.tokenizer.StanfordTokenizer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * StanfordTokenizerPerformanceTest
 * de.iisys.ocr.test.performance.tokenizer
 * Created by reza on 22.08.14.
 */
public class StanfordTokenizerPerformanceTest extends BaseTest {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @Test
    @BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 10)
    public void testTokenizerPerformance() {
        final String content = StringUtils.repeat(mSampleTokenizerText, 5000);

        ITokenizer tokenizer = new StanfordTokenizer(content);

        while (tokenizer.hasNext()) tokenizer.next();
    }
}
