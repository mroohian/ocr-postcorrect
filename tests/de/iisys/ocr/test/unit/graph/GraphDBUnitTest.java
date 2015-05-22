package de.iisys.ocr.test.unit.graph;

import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import de.iisys.ocr.test.core.graph.EmptyLocalGraphDBTest;
import de.iisys.ocr.tokenizer.StanfordTokenizer;
import org.junit.Test;

/**
 * GraphDBUnitTest
 * de.iisys.de.iisys.ocr.test.unit.graph
 * Created by reza on 18.08.14.
 */
public class GraphDBUnitTest extends EmptyLocalGraphDBTest {
    @Test
    public void testDBConnection() {
        System.out.println("testDBConnection");
        assert mGraphDbUnit.getGraphDB().isOpen() && mGraphDbUnit.getOrientGraph() != null;
    }

    @Test
    public void testDBInsert() {
        System.out.println("testDBInsert");

        OrientBaseGraph orientGraph = mGraphDbUnit.getOrientGraph();
        // Test inserting a record
        String value = "a";
        OrientVertex vertex = orientGraph.addVertex("class:Word", "value", value);

        assert vertex != null;

        orientGraph.commit();
    }

    @Test
    public void testDBPopulateData() {
        final String content = "This is a test. This is another test. This is the final test.";
        mGraphDbUnit.getGraphDB().populateDataComplete(new StanfordTokenizer(content));
        //mGraphDbUnit.getGraphDB().populateDataFollowedBy(new StanfordTokenizer(content));
    }
}
