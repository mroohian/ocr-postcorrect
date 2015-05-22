package de.iisys.levdistcorpa.test.utils;

import de.iisys.levdistcorpa.test.BaseTest;
import de.iisys.levdistcorpa.trie.ConfusionMatrix;
import de.iisys.levdistcorpa.trie.IConfusionMatrix;
import de.iisys.levdistcorpa.utils.Utils;
import org.junit.Test;

/**
 * LevenshteinTest
 * Created by reza on 28.01.15.
 */
public class LevenshteinTest extends BaseTest {
    @Test
    public void test() {
        final String input = "text";
        final String word = "test";

        double ed = Utils.levenshtein(input, word);

        assert (ed == 1.0);
    }

    @Test
    public void testWeighted() {
        final IConfusionMatrix confusionMatrix = new ConfusionMatrix();
        final String input = "text";
        final String word = "test";
        final String word1 = "teSt";

        double ed = Utils.levenshteinWeighted(input, word, confusionMatrix);
        assert (ed == 1.2);

        ed = Utils.levenshteinWeighted(input, word1, confusionMatrix);
        assert (ed == 1.3);
    }
}
