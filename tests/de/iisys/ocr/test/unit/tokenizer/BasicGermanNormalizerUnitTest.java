package de.iisys.ocr.test.unit.tokenizer;

import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.tokenizer.BasicGermanNormalizer;
import de.iisys.ocr.tokenizer.INormalizer;
import de.iisys.ocr.tokenizer.ITokenizer;
import de.iisys.ocr.tokenizer.StanfordTokenizer;
import org.junit.Test;

/**
 * BasicNormalizerUnitTest
 * de.iisys.ocr.test.unit.tokenizer
 * Created by reza on 22.08.14.
 */
public class BasicGermanNormalizerUnitTest extends BaseTest {
    @Test
    public void testNormalize() {
        INormalizer normalizer = new BasicGermanNormalizer();

        assert normalizer.normalize("Test").equals("Test");
        assert normalizer.normalize("12:12").equals("00:00");
        assert normalizer.normalize("28.04.2014").equals("00.00.0000");
        assert normalizer.normalize("!%#@^").equals("!%#@^");
    }

    @Test
    public void testTokenizerBadChars() {
        final String content = "k�nnte m�dchen k�hl spa�";

        ITokenizer tokenizer = new StanfordTokenizer(new BasicGermanNormalizer(), content);

        int n = 0;
        for (//noinspection UnusedDeclaration
             String part; tokenizer.hasNext(); n++ ) {
            //noinspection UnusedAssignment
            part = tokenizer.next();
            //System.out.println(part);
        }

        assert n == 4;
    }
}
