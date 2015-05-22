package de.iisys.ocr.possequence.property;

import de.iisys.ocr.possequence.property.core.IProperty;

import java.util.List;

/**
 * PunctuationProperty
 * Created by reza on 26.01.15.
 */
public class PunctuationProperty implements IProperty {
    private String puncs = ".,:;";

    @Override
    public int[] getValue(List<String> input) {
        int[] result = new int[input.size()];
        for (int j = 0; j < input.size(); j++) {
            String word = input.get(j);
            if (word == null) continue;
            result[j] = (puncs.contains(word) ? 1 : 0);
        }

        return result;
    }
}