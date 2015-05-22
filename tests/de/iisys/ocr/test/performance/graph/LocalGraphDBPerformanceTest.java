package de.iisys.ocr.test.performance.graph;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.graph.GraphDB;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.corpa.WordPOSCorpaUnit;
import de.iisys.ocr.test.core.graph.LocalGraphDBUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;

/**
 * LocalGraphDBPerformanceTest
 * de.iisys.ocr.test.performance.graph
 * Created by reza on 04.09.14.
 */
public class LocalGraphDBPerformanceTest extends BaseTest {
    private static WordPOSCorpaUnit mWordPOSCorpaUnit;
    private static LocalGraphDBUnit mGraphDBUnit;

    @BeforeClass
    public static void setup() throws IOException, ClassNotFoundException {
        mWordPOSCorpaUnit = new WordPOSCorpaUnit(mPOSAlphabetFile, mWordAlphabetFile, mWordPOSMappingFile);
        mGraphDBUnit = new LocalGraphDBUnit(mLocalGraphDBPath, mLocalUser, mLocalPassword, false);
    }

    @AfterClass
    public static void tearDown() {
        mGraphDBUnit.dispose();
    }
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @Test
    @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
    public void testGetWordRIDPerformance() {
        int[] results = { 0, 0 };
        GraphDB graphDB = mGraphDBUnit.getGraphDB();
        for (INode node : mWordPOSCorpaUnit.getCorpa().getAllNodes()) {
            OIdentifiable rid = graphDB.getWordRID(mWordPOSCorpaUnit.getCorpa().getWordById(node.getWordId()));
            if (rid != null) results[0]++; else results[1]++;
        }

        System.out.println(String.format("Querying %d words, %d is found and %d is not.", mWordPOSCorpaUnit.getCorpa().getLength(), results[0], results[1]));
    }
}
