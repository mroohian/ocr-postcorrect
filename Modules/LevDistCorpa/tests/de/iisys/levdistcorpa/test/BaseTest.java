package de.iisys.levdistcorpa.test;

import de.iisys.levdistcorpa.types.INode;

/**
 * de.iisys.levdistcorpa.test
 * Created by reza on 21.10.14.
 */
public class BaseTest {
    protected class SampleNode implements INode {
        private final int wordId;

        public SampleNode(int wordId) {
            this.wordId = wordId;
        }

        @Override
        public int getWordId() {
            return wordId;
        }

        @Override
        public int getRank() {
            return 0;
        }

        @Override
        public INode makeNew(int wordId) {
            return new SampleNode(wordId);
        }
    }
}
