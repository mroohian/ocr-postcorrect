package de.iisys.ocr.possequence.property;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.ocr.possequence.property.core.IProperty;

import java.util.List;

/**
 * WordIdProperty
 * Created by reza on 25.01.15.
 */
public class WordIdProperty implements IProperty {
    private final Corpa corpa;

    public WordIdProperty(Corpa corpa) {
        this.corpa = corpa;
    }

    @Override
    public int[] getValue(List<String> input) {
        int[] result = new int[input.size()];
        for (int j = 0; j < input.size(); j++) {
            String word = input.get(j);
            if (word == null) {
                result[j] = -1; // invalid ID
                continue;
            }

            int wordId;
            try {
                wordId = corpa.getWordId(word);
            } catch (NullPointerException ex) {
                wordId = -1; // invalid ID
            }
            result[j] = wordId;
        }

        return result;
    }
}
