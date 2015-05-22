package de.iisys.ocr.test.unit.textalignment;

import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.textalignment.DefaultLevenshteinAlignerContext;
import de.iisys.ocr.textalignment.LevenshteinTextAligner;
import de.iisys.ocr.textalignment.LevenshteinTextAlignerResultSet;
import org.junit.Test;

/**
 * de.iisys.ocr.test.unit.textalignment
 * Created by reza on 25.11.14.
 */
public class LevenshteinTextAlignerUnitTests extends BaseTest {
    @Test
    public void testLevAlign() {
        final String input =  "Thhsis a ttst. fsffsstusffs ";
        final String output = "This is a test . more stuff ";

        LevenshteinTextAligner aligner = new LevenshteinTextAligner(new DefaultLevenshteinAlignerContext());

        LevenshteinTextAlignerResultSet result = aligner.align(input, output);
        System.out.println("OCR:" + input);
        System.out.println("Original:" + output);
        System.out.println();
        System.out.println(result.toString());
    }
}
