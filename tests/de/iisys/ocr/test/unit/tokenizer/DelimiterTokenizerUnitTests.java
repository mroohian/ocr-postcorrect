package de.iisys.ocr.test.unit.tokenizer;

import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.tokenizer.DelimiterTokenizer;
import de.iisys.ocr.tokenizer.ITokenizer;
import org.junit.Test;

/**
 * DelimiterTokenizerUnitTests
 * de.iisys.ocr.test.unit.tokenizer
 * Created by reza on 16.09.14.
 */
public class DelimiterTokenizerUnitTests extends BaseTest {
    @Test
    public void testTokenizer() {
        ITokenizer tokenizer = new DelimiterTokenizer(mSampleTokenizerText);

        int n = 0;
        for (//noinspection UnusedDeclaration
                String part; tokenizer.hasNext(); n++ ) {
            //noinspection UnusedAssignment
            part = tokenizer.next();
            //System.out.println(part);
        }

        assert n == 14;
    }
}
