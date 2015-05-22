package de.iisys.ocr.test.performance.graph;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import de.iisys.ocr.graph.GraphDB;
import de.iisys.ocr.test.core.graph.EmptyLocalGraphDBTest;
import de.iisys.ocr.tokenizer.StanfordTokenizer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

/**
 * GraphDBPerformanceTest
 * de.iisys.de.iisys.ocr.test.performance.graph
 * Created by reza on 18.08.14.
 */
public class GraphDBPerformanceTest extends EmptyLocalGraphDBTest {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @Test
    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1)
    public void testDBInsertVertexNoTxPerformance() {
        System.out.println("testDBInsertVertexNoTxPerformance start: " + new Date().toString());
        Random random = new Random();
        OrientBaseGraph orientGraph = mGraphDbUnit.getOrientGraph();

        for (int i = 0; i < 1000000; i++) {
            // Test inserting a record
            String value = new BigInteger(130, random).toString(32);
            Vertex vertex = orientGraph.addVertex("class:Word", "value", value);

            assert vertex != null;

            orientGraph.commit();
        }
        System.out.println("testDBInsertVertexNoTxPerformance end: " + new Date().toString());
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1)
    public void testDBInsertVertexTxPerformance() {
        System.out.println("testDBInsertVertexTxPerformance start: " + new Date().toString());
        mGraphDbUnit.getGraphDB().setTransactionalMode(true);
        OrientBaseGraph orientGraph = mGraphDbUnit.getOrientGraph();
        System.out.println("Switched to transactional mode.");
        Random random = new Random();

        for (int i = 0; i < 1000000; i++) {
            // Test inserting a record
            String value = new BigInteger(130, random).toString(32);
            Vertex vertex = orientGraph.addVertex("class:Word", "value", value);

            assert vertex != null;

            orientGraph.commit();
        }
        System.out.println("testDBInsertVertexTxPerformance end: " + new Date().toString());
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1)
    public void testDBInsertVertexAndEdgeLinearPerformance() {
        System.out.println("testDBInsertVertexAndEdgeLinearPerformance start: " + new Date().toString());
        Random random = new Random();
        GraphDB graphDB = mGraphDbUnit.getGraphDB();
        OrientBaseGraph orientGraph = mGraphDbUnit.getOrientGraph();

        Vertex before = null;
        for (int i =0; i < 1000000; i++) {
            String value = new BigInteger(130, random).toString(32);
            Vertex current = orientGraph.addVertex("class:Word", "value", value);
            assert current != null;

            if (before != null) {
                // add edge between before and current
                Edge edge = graphDB.getFollowedByEdge(current, before);

                if (edge == null) {
                    edge = orientGraph.addEdge(null, current, before, GraphDB.EDGE_TYPE_FOLLOWED_BY);
                    assert edge != null;
                    edge.setProperty("freq", 1);
                } else {
                    edge.setProperty("freq", (Integer)edge.getProperty("freq") + 1);
                }
            }

            before = current;
            orientGraph.commit();
        }

        System.out.println("testDBInsertVertexAndEdgeLinearPerformance end: " + new Date().toString());
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1)
    public void testDBInsertVertexAndEdgeWithLookupPerformance() {
        System.out.println("testDBInsertVertexAndEdgeWithLookupPerformance start: " + new Date().toString());
        Random random = new Random();
        GraphDB graphDB = mGraphDbUnit.getGraphDB();
        OrientBaseGraph orientGraph = mGraphDbUnit.getOrientGraph();

        String beforeValue = null;
        for (int i =0; i < 1000000; i++) {
            String value = new BigInteger(130, random).toString(32);
            Vertex current = graphDB.getWord(value);

            if (current == null) {
                current = orientGraph.addVertex("class:Word", "value", value);
                current.setProperty("freq", 1);
            } else {
                current.setProperty("freq", (Integer)current.getProperty("freq") + 1);
            }
            //noinspection ConstantConditions
            assert current != null;

            Vertex before = graphDB.getWord(beforeValue);
            if (before != null) {
                // add edge between before and current
                Edge edge = graphDB.getFollowedByEdge(current, before);

                if (edge == null) {
                    edge = orientGraph.addEdge(null, current, before, GraphDB.EDGE_TYPE_FOLLOWED_BY);
                    assert edge != null;
                    edge.setProperty("freq", 1);
                } else {
                    edge.setProperty("freq", (Integer)edge.getProperty("freq") + 1);
                }
            }

            beforeValue = value;
            orientGraph.commit();
        }

        System.out.println("testDBInsertVertexAndEdgeWithLookupPerformance end: " + new Date().toString());
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 200, warmupRounds = 10)
    public void testDBPopulatePerformance() {
        final String content = "This is a test. This is another test. This is the final test.";
        mGraphDbUnit.getGraphDB().populateDataComplete(new StanfordTokenizer(content));
        //mGraphDbUnit.getGraphDB().populateDataFollowedBy(new StanfordTokenizer(content));
    }
}
