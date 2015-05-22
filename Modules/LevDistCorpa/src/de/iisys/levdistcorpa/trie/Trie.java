package de.iisys.levdistcorpa.trie;

import de.iisys.levdistcorpa.types.INode;

/**
 * Trie
 * de.iisys.levdistcorpa.trie
 * Created by reza on 28.07.14.
 */
public class Trie {
    private TrieNode mRoot;

    public Trie() {
        mRoot = new TrieNode('\0');
    }

    public TrieNode getRoot() {
        return mRoot;
    }

    public void addWord(String word, INode node, int flag) throws RuntimeException {
        TrieNode current = mRoot;

        if (word.length() == 0) {
            current.setWordMarker(); // an empty word
            return;
        }

        if (word.length() > TrieNode.MAX_WORD_SIZE) {
            throw new RuntimeException("TRIE_ERROR_MAX_LENGTH_EXCEEDED");
        }

        for (int i = 0; i < word.length(); i++ ) {
            TrieNode child = current.findChild(word.charAt(i));

            if (child == null) {
                child = new TrieNode(word.charAt(i));
                current.appendChild(child);
            }

            current = child;
        }

        current.setNode(node);
        current.setFlag(flag);
        current.setWordMarker();

    }
}
