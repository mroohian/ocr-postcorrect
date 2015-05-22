package de.iisys.ocr.test.unit.utils;

import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.utils.TextUtils;
import org.junit.Test;

/**
 * TextUtilsUnitTest
 * de.iisys.de.iisys.ocr.test.unit.utils
 * Created by reza on 18.08.14.
 */
public class TextUtilsUnitTest extends BaseTest {
    @Test
    public void testFormatColumnString() {
        String result = TextUtils.formatColumnString("test", 6);
        assert result.equals("test  \t");
    }
}
