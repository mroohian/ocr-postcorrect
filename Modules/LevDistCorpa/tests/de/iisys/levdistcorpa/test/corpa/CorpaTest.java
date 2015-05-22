package de.iisys.levdistcorpa.test.corpa;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.test.BaseTest;
import org.junit.Test;

import java.io.IOException;

/**
 * CorpaTest
 * de.iisys.levdistcorpa.test.corpa
 * Created by reza on 21.10.14.
 */
public class CorpaTest extends BaseTest {
    protected class SampleCorpa extends Corpa {
        @Override
        public void load() throws IOException {
            addEntry(new SampleNode(addWord("Birnen")), 0);
            addEntry(new SampleNode(addWord("birnen")), 0);
            addEntry(new SampleNode(addWord("Baum")), 0);
            addEntry(new SampleNode(addWord("baum")), 0);
        }
    }

    private final SampleCorpa mCorpa;

    public CorpaTest() throws IOException {
        mCorpa = new SampleCorpa();
        mCorpa.load();
    }

    @Test
    public void testHasWord() throws Exception {
        assert mCorpa.hasWord("Birnen");
        assert !mCorpa.hasWord("Test");
    }

    @Test
    public void testHasCompoundWord() throws Exception {
        assert mCorpa.hasCompoundWord("Birnenbaum");
        assert mCorpa.hasCompoundWord("BaumBirnen");
        assert !mCorpa.hasCompoundWord("BBirnen");
    }

    @Test
    public void testHasWordOrPrefix() throws Exception {
        assert mCorpa.hasWordOrPrefix("Bir");
        assert !mCorpa.hasWordOrPrefix("Baa");
    }

}
