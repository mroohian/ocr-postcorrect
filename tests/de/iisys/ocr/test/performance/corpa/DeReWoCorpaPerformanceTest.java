package de.iisys.ocr.test.performance.corpa;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.DeReWoCorpa;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.DeReWoCorpaUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * de.iisys.ocr.test.performance.corpa
 * Created by reza on 19.08.14.
 */
public class DeReWoCorpaPerformanceTest extends BaseTest {
    private static DeReWoCorpaUnit mDeReWoCorpaUnit;

    @BeforeClass
    public static void setup() {
        mDeReWoCorpaUnit = new DeReWoCorpaUnit(mCorpusFile);
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testFindSimilarWordsPerformance() throws Exception {
        System.out.println("testFindSimilarWordsPerformance start: " + new Date().toString());
        final int cutoff = 2;

        DeReWoCorpa corpa = mDeReWoCorpaUnit.getCorpa();
        Date start = new Date();
        for (INode node : corpa.getAllNodes()) {
            corpa.findSimilarWords(corpa.getWordById(node.getWordId()), cutoff);
        }
        Date end = new Date();
        double difference = end.getTime() - start.getTime();
        System.out.println("average time on " + corpa.getLength() + " records: " + difference / (double)corpa.getLength() + " ms");
        System.out.println("testFindSimilarWordsPerformance end: " + new Date().toString());
    }
}
