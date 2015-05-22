package de.iisys.ocr.test.performance.corpa;

import de.iisys.levdistcorpa.trie.ConfusionMatrix;
import de.iisys.levdistcorpa.trie.IConfusionMatrix;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.levdistcorpa.utils.Utils;
import de.iisys.ocr.corpa.WordPOSCorpa;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.WordPOSCorpaUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

/**
 * WordPOSCorpaPerformanceTest
 * Created by reza on 28.01.15.
 */
public class WordPOSCorpaPerformanceTest extends BaseTest {
    private static WordPOSCorpaUnit wordPOSCorpaUnit;

    @BeforeClass
    public static void setup() throws IOException, ClassNotFoundException {
        wordPOSCorpaUnit = new WordPOSCorpaUnit(mPOSAlphabetFile, mWordAlphabetFile, mWordPOSMappingFile);
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testFindSimilarWordsNaivePerformance() throws Exception {
        System.out.println("testFindSimilarWordsNaivePerformance start: " + new Date().toString());
        final int cutoff = 2;

        IConfusionMatrix confusionMatrix = new ConfusionMatrix();
        WordPOSCorpa corpa = wordPOSCorpaUnit.getCorpa();
        final long length = corpa.getLength();
        Date start = new Date();
        int n = 100;
        for (INode node : corpa.getAllNodes()) {
            if (n-- < 0) break;
            String input = corpa.getWordById(node.getWordId());

            for (INode node1 : corpa.getAllNodes()) {
                if (node.getWordId() == node1.getWordId()) continue;
                String word = corpa.getWordById(node1.getWordId());
                Utils.levenshteinWeighted(input, word, confusionMatrix);
            }
        }
        Date end = new Date();
        double difference = end.getTime() - start.getTime();
        difference /= 100.0;
        difference *= length;
        System.out.println("average time on " + length + " records: " + difference / (double)length + " ms");
        System.out.println("testFindSimilarWordsNaivePerformance end: " + new Date().toString());
    }


    @Test
    public void testFindSimilarWordsPerformance() throws Exception {
        System.out.println("testFindSimilarWordsPerformance start: " + new Date().toString());
        final int cutoff = 2;

        WordPOSCorpa corpa = wordPOSCorpaUnit.getCorpa();
        final long length = corpa.getLength();
        Date start = new Date();
        for (INode node : corpa.getAllNodes()) {
            corpa.findSimilarWords(corpa.getWordById(node.getWordId()), cutoff);
        }
        Date end = new Date();
        double difference = end.getTime() - start.getTime();
        System.out.println("average time on " + length + " records: " + difference / (double)length + " ms");
        System.out.println("testFindSimilarWordsPerformance end: " + new Date().toString());
    }
}
