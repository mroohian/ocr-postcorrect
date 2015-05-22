package de.iisys.ocr.test.unit.tokenizer;

import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.tokenizer.ITokenizer;
import de.iisys.ocr.tokenizer.StanfordTokenizer;
import org.junit.Test;

/**
 * StanfordTokenizerUnitTest
 * de.iisys.ocr.test.unit.tokenizer
 * Created by reza on 22.08.14.
 */
public class StanfordTokenizerUnitTest extends BaseTest {
    @Test
    public void testTokenizer() {
        ITokenizer tokenizer = new StanfordTokenizer(mSampleTokenizerText);

        int n = 0;
        for (//noinspection UnusedDeclaration
             String part; tokenizer.hasNext(); n++ ) {
            //noinspection UnusedAssignment
            part = tokenizer.next();
            //System.out.println(part);
        }

        assert n == 22;
    }
}
