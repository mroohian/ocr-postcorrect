package de.iisys.ocr.corpa;

import de.iisys.levdistcorpa.trie.TrieNode;
import de.iisys.ocr.types.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * DeReWoCorpa
 * de.iisys.ocr.corpa
 * Created by reza on 28.07.14.
 */
public class DeReWoCorpa extends TokenCorpa {
    private final String mCorpusFile;

    public DeReWoCorpa(String corpusFile) {
        super();
        mCorpusFile = corpusFile;
    }

    private void addPunctuations() {
        addEntry(new Token(addWord("!"), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord("."), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord(","), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord(";"), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord(":"), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord("'"), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord("\""), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord("\\"), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord("/"), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord("("), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
        addEntry(new Token(addWord(")"), 0), TrieNode.TRIENODE_FLAG_PUNCTUATION);
    }

    public void load(boolean shouldAddPunctuations) {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error opening corpus file.");
        }

        if (shouldAddPunctuations) addPunctuations();
    }

    @Override
    public void load() throws IOException {
        String line;
        int n = 0;

        BufferedReader reader = new BufferedReader(new FileReader(mCorpusFile));

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.charAt(0) == '#') continue;
            String[] parts = line.split("\\s+");
            String word = parts[0];
            int value = Integer.parseInt(parts[1]);
            addEntry(new Token(addWord(word), value), TrieNode.TRIENODE_FLAG_NORMAL);
            n++;
            String wordLower = word.toLowerCase();
            if (!word.equals(wordLower)) {
                addEntry(new Token(addWord(wordLower), value), TrieNode.TRIENODE_FLAG_LOWERCASE);
                n++;
            }
        }

        //setLength(n);
    }
}
