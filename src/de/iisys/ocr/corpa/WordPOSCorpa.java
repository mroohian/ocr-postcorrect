package de.iisys.ocr.corpa;

import de.iisys.levdistcorpa.alphabet.POSAlphabet;
import de.iisys.levdistcorpa.alphabet.WordAlphabet;
import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.trie.TrieNode;
import de.iisys.ocr.types.WordPOSInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * WordPOSCorpa
 * Created by reza on 12.01.15.
 */
public class WordPOSCorpa extends TokenCorpa {
    private final String wordPOSMappingFile;

    public WordPOSCorpa(String posAlphabetFile, String wordAlphabetFile, String wordPOSMappingFile) throws IOException, ClassNotFoundException {
        super( new WordAlphabet(wordAlphabetFile), new POSAlphabet(posAlphabetFile));

        this.wordPOSMappingFile = wordPOSMappingFile;
    }

    @Override
    public void load() throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(wordPOSMappingFile));

        // Ignored
        /*long totalWords = */ois.readLong();

        ArrayList<WordPOSInfo> sortedWordPOSMappings;
        try {
            sortedWordPOSMappings = (ArrayList<WordPOSInfo>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
        ois.close();

        for (WordPOSInfo wordPOSInfo : sortedWordPOSMappings) {
            addEntry(wordPOSInfo, TrieNode.TRIENODE_FLAG_NORMAL);
        }
    }
}
