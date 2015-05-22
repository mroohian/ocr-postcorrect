package de.iisys.ocr.test.unit.corpa;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.DeReWoCorpaUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * DeReWoCorpaUnitTest
 * de.iisys.ocr.test.unit.corpa
 * Created by reza on 19.08.14.
 */
public class DeReWoCorpaUnitTest extends BaseTest {
    private static DeReWoCorpaUnit mDeReWoCorpaUnit;

    @BeforeClass
    public static void setup() {
        mDeReWoCorpaUnit = new DeReWoCorpaUnit(mCorpusFile);
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testGetLength() throws Exception {
        assert mDeReWoCorpaUnit.getCorpa().getLength() > 0;
    }

    @Test
    public void testFindSimilarWords() throws Exception {
        double cutoff = 2.0;
        Map<Double, List<INode>> results = mDeReWoCorpaUnit.getCorpa().findSimilarWords("datteln", cutoff);
        assert results.size() == 21;

        List<INode> dist0 = results.get(0.0);
        assert dist0 != null && dist0.size() > 0;
        assert mDeReWoCorpaUnit.getCorpa().getWordById(dist0.get(0).getWordId()).equals("datteln");

        List<INode> dist1 = results.get(1.3);
        assert dist1 != null && dist1.size() > 0;
    }

    @Test
    public void testHavePunctuation() throws Exception {
        assert mDeReWoCorpaUnit.getCorpa().hasWord(".");
    }
}