package de.iisys.levdistcorpa.test.trie;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.test.BaseTest;
import de.iisys.levdistcorpa.types.INode;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * TrieSearchConfusionTest
 * de.iisys.levdistcorpa.test.trie
 * Created by reza on 21.10.14.
 */
public class TrieSearchConfusionTest extends BaseTest {
    private class SampleCorpa extends Corpa {
        @Override
        public void load() throws IOException {
            addEntry(new SampleNode(addWord("this")), 0);
            addEntry(new SampleNode(addWord("thIs")), 0);
            addEntry(new SampleNode(addWord("is")), 0);
            addEntry(new SampleNode(addWord("a")), 0);
            addEntry(new SampleNode(addWord("another")), 0);
            addEntry(new SampleNode(addWord("test")), 0);
            addEntry(new SampleNode(addWord("thuse")), 0);
            addEntry(new SampleNode(addWord("th")), 0);
        }
    }

    private final SampleCorpa mCorpa;

    public TrieSearchConfusionTest() throws IOException {
        mCorpa = new SampleCorpa();
        mCorpa.load();
    }

    @Test
    public void testFindSimilarWordWithConfusionMatrix() {
        final int cutoff = 2;
        Map<Double,List<INode>> result = mCorpa.findSimilarWords("thus", 2);

        assert result.size() == 21;

        for (int i = 0; i < 21; i++) {
            if (i != 11 && i != 12 && i != 13) {
                double dist = i / 10.0;
                assert result.get(dist).size() == 0;
            }
        }

        assert result.get(1.1).size() == 1;
        assert result.get(1.2).size() == 1;
        assert result.get(1.3).size() == 1;

        assert mCorpa.getWordById(result.get(1.1).get(0).getWordId()) == "thuse";
        assert mCorpa.getWordById(result.get(1.2).get(0).getWordId()) == "this";
        assert mCorpa.getWordById(result.get(1.3).get(0).getWordId()) == "thIs";
    }

    @Test
    public void testFindSimilarCompoundWordWithConfusionMatrix() {

    }
}
