package de.iisys.levdistcorpa.test.trie;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.test.BaseTest;
import de.iisys.levdistcorpa.types.INode;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * TrieSearchTest
 * de.iisys.levdistcorpa.test.corpa
 * Created by reza on 19.08.14.
 */
public class TrieSearchTest extends BaseTest {
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

    public TrieSearchTest() throws IOException {
        mCorpa = new SampleCorpa();
        mCorpa.load();
    }

    @Test
    public void testFindSimilarWordsDebug() throws Exception {
        double cutoff = 2.0;
        Map<Double, List<INode>> results = mCorpa.findSimilarWordsNoConfusionMatrixDebug("birne", cutoff);

        assert results.size() == cutoff + 1;

        List<INode> dist0 = results.get(0.0);
        assert dist0 != null && dist0.size() == 0;

        List<INode> dist1 = results.get(1.0);
        assert dist1 != null && dist1.size() == 1;

        List<INode> dist2 = results.get(2.0);
        assert dist2 != null && dist2.size() == 1;
    }

    @Test
    public void testFindSimilarWords() throws Exception {
        double cutoff = 2.0;
        Map<Double, List<INode>> results = mCorpa.findSimilarWordsNoConfusionMatrix("birne", cutoff);

        assert results.size() == cutoff + 1;

        List<INode> dist0 = results.get(0.0);
        assert dist0 != null && dist0.size() == 0;

        List<INode> dist1 = results.get(1.0);
        assert dist1 != null && dist1.size() == 1;

        List<INode> dist2 = results.get(2.0);
        assert dist2 != null && dist2.size() == 1;
    }

    @Test
    public void testFindSimilarCompoundWords() throws Exception {
        double cutoff = 2.0;
        Map<Double, List<INode>> results = mCorpa.findSimilarCompoundWordsNoConfusionMatrix("Birnebaum", cutoff);

        assert results.size() == cutoff + 1;

        List<INode> dist0 = results.get(0.0);
        assert dist0 != null && dist0.size() == 0;

        List<INode> dist1 = results.get(1.0);
        assert dist1 != null && dist1.size() == 1;

        List<INode> dist2 = results.get(2.0);
        assert dist2 != null && dist2.size() == 2;
    }
}