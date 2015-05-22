package de.iisys.ocr.test.performance.tokenizer;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.tokenizer.BasicGermanNormalizer;
import de.iisys.ocr.tokenizer.INormalizer;
import de.iisys.ocr.tokenizer.ITokenizer;
import de.iisys.ocr.tokenizer.StanfordTokenizer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * BasicNormalizerPerformanceTest
 * de.iisys.ocr.test.performance.tokenizer
 * Created by reza on 22.08.14.
 */
public class BasicGermanNormalizerPerformanceTest extends BaseTest {
    @Rule
    public TestRule benchmarkRule = new BenchmarkRule();

    @Test
    @BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 10)
    public void testTokenizerPerformance() {
        final INormalizer normalizer = new BasicGermanNormalizer();
        final String content = StringUtils.repeat("This is a test 28.04.2014 . !%#@^ Sample word's letters, \"Quotation\" and punctuations! I'd like to test 12:12.", 5000);

        ITokenizer tokenizer = new StanfordTokenizer(normalizer, content);

        while (tokenizer.hasNext()) tokenizer.next();
    }
}
