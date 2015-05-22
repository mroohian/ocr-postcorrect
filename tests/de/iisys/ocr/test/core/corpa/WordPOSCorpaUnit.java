package de.iisys.ocr.test.core.corpa;

import de.iisys.ocr.corpa.WordPOSCorpa;

import java.io.File;
import java.io.IOException;

/**
 * WordPOSCorpaUnit
 * Created by reza on 12.01.15.
 */
public class WordPOSCorpaUnit {
    private WordPOSCorpa mCorpa;

    public WordPOSCorpaUnit(String posAlphabetFile, String wordAlphabetFile, String wordPOSMappingFile) throws IOException, ClassNotFoundException {
        assert new File(posAlphabetFile).exists();
        assert new File(wordAlphabetFile).exists();
        assert new File(wordPOSMappingFile).exists();

        mCorpa = new WordPOSCorpa(posAlphabetFile, wordAlphabetFile, wordPOSMappingFile);
        mCorpa.load();
    }

    public WordPOSCorpa getCorpa() {
        return mCorpa;
    }
}
